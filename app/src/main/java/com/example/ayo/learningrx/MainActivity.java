package com.example.ayo.learningrx;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.AutoCompleteTextView;

import com.example.ayo.learningrx.model.Actor;
import com.example.ayo.learningrx.model.ActorResults;
import com.example.ayo.learningrx.model.Movie;
import com.example.ayo.learningrx.model.MovieResults;
import com.example.ayo.learningrx.retrofit.MovieApi;
import com.example.ayo.learningrx.retrofit.MovieService;
import com.jakewharton.rxbinding2.widget.AdapterViewItemClickEvent;
import com.jakewharton.rxbinding2.widget.RxAutoCompleteTextView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private String id1 = "";
    private String id2 = "";

    private MovieApi movieApi;

    PublishProcessor<List<Movie>> resultEmitterSubject;
    MovieListAdapter movieListAdapter;
    ActorListArrayAdapter actorListArrayAdapter;

    @BindView(R.id.movie_list)
    RecyclerView movieListRecyclerView;

    @BindView(R.id.search_term_1)
    AutoCompleteTextView searchTerm1;

    @BindView(R.id.search_term_2)
    AutoCompleteTextView searchTerm2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        movieApi = MovieService.createMovieService();
        resultEmitterSubject = PublishProcessor.create();
        resultEmitterSubject.subscribe(this::setUpRecyclerView);

        addOnAutoCompleteTextViewTextChangedObserver(searchTerm1);
        addOnAutoCompleteTextViewTextChangedObserver(searchTerm2);

        addOnAutoCompleteTextViewItemClickedListener(searchTerm1);
        addOnAutoCompleteTextViewItemClickedListener(searchTerm2);

    }

    private void addOnAutoCompleteTextViewItemClickedListener(AutoCompleteTextView autoCompleteTextView) {
        Observable<Actor> adapterViewItemClickEventObservable =
                RxAutoCompleteTextView.itemClickEvents(autoCompleteTextView)
                        .map(adapterViewItemClickEvent -> (Actor) autoCompleteTextView.getAdapter().getItem(adapterViewItemClickEvent.position()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry();

        adapterViewItemClickEventObservable.subscribe(new Observer<Actor>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Actor actor) {
                switch (autoCompleteTextView.getId()){
                    case R.id.search_term_1: id1 = actor.getId(); break;
                    case R.id.search_term_2: id2 = actor.getId(); break;
                }

                if(!id1.equals("") && !id2.equals("")){
                    Call<MovieResults> call = movieApi.getMovies(id1+","+id2);
                    call.enqueue(new Callback<MovieResults>() {
                        @Override
                        public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
                            resultEmitterSubject.onNext(response.body().getMovies());
                        }

                        @Override
                        public void onFailure(Call<MovieResults> call, Throwable t) {

                        }
                    });
                }

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void addOnAutoCompleteTextViewTextChangedObserver(AutoCompleteTextView autoCompleteTextView) {

        Observable<ActorResults> autoCompleteResponseObservable =
                RxTextView.textChangeEvents(autoCompleteTextView)
                        .debounce(400, TimeUnit.MILLISECONDS) // default Scheduler is Computation
                        .map(textChange -> textChange.text().toString())
                        .filter(s -> s.length() > 3)
                        .observeOn(Schedulers.io())
                        .switchMap(textChangeString -> movieApi.getObservableActors(textChangeString))
                        .observeOn(AndroidSchedulers.mainThread())
                        .retry();

        autoCompleteResponseObservable.subscribe(new Observer<ActorResults>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ActorResults actorResults) {

                if(actorResults.getActors().isEmpty() || autoCompleteTextView.getText().toString().trim().equals(""))
                    resultEmitterSubject.onNext(Collections.emptyList());

                List<Actor> actors = actorResults.getActors();
                actorListArrayAdapter = new ActorListArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, actors);
                autoCompleteTextView.setAdapter(actorListArrayAdapter);
                String enteredText = autoCompleteTextView.getText().toString();
                if (actors.size() >= 1 && enteredText.equals(actors.get(0).getName())) {
                    autoCompleteTextView.dismissDropDown();
                } else {
                    autoCompleteTextView.showDropDown();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void setUpRecyclerView(List<Movie> movies) {
        movieListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        movieListAdapter = new MovieListAdapter(movies);
        movieListRecyclerView.setAdapter(movieListAdapter);
    }


}

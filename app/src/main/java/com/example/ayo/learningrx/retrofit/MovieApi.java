package com.example.ayo.learningrx.retrofit;



import com.example.ayo.learningrx.model.ActorResults;
import com.example.ayo.learningrx.model.MovieResults;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface MovieApi {

    @GET("search/person")
    Observable<ActorResults> getObservableActors(@Query("query") String query);

    @GET("search/person")
    Call<ActorResults> getActors(@Query("query") String query);

    @GET("discover/movie")
    Observable<MovieResults> getObservableMovies(@Query("with_people") String withPeople);

    @GET("discover/movie")
    Call<MovieResults> getMovies(@Query("with_people") String withPeople);
}

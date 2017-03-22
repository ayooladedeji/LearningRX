package com.example.ayo.learningrx;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.ayo.learningrx.model.Actor;

import java.util.ArrayList;
import java.util.List;

public class ActorListArrayAdapter extends ArrayAdapter<Actor> {

    private Context context;
    private List<Actor> actors, suggestions, tempItems;

    public ActorListArrayAdapter(Context context, int resource, List<Actor> actors) {
        super(context, resource, actors);

        this.context = context;
        this.actors = actors;
        tempItems = new ArrayList<>(actors); // this makes the difference.
        suggestions = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Actor actor = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_actor, parent, false);
        }
        // Lookup view for data population
        TextView actorName = (TextView) convertView.findViewById(R.id.actor_name);
        // Populate the data into the template view using the data object
        actorName.setText(actor.getName());
        // Return the completed view to render on screen
        return convertView;
    }

    public int getCount() {
        return actors.size();
    }

    public Actor getActor(int position){
        return actors.get(position);
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Actor) resultValue).getName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Actor actor : tempItems) {
                    if (actor.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(actor);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Actor> filterList = (ArrayList<Actor>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Actor actor : filterList) {
                    add(actor);
                    notifyDataSetChanged();
                }
            }
        }
    };

}

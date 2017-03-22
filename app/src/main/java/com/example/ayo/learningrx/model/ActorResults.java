package com.example.ayo.learningrx.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ActorResults {

    @SerializedName("results")
    private List<Actor> actors;

    public List<Actor> getActors() {
        return actors;
    }
}

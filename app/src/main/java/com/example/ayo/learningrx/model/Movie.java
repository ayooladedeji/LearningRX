package com.example.ayo.learningrx.model;

import com.google.gson.annotations.SerializedName;

public class Movie {

    @SerializedName("backdrop_path")
    private String backdropPath;
    private String title;

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getTitle() {
        return title;
    }
}

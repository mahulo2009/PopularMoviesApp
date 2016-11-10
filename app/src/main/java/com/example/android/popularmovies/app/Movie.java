package com.example.android.popularmovies.app;

/**
 * Created by mhuertas on 9/11/16.
 */

public class Movie {

    private int id;
    private String title;
    private String poster_path;
    private String overview;
    private String vote_average;
    private String release_date;

    public Movie() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String toString() {
        return id + " : " + title + " - " + poster_path + " - " + vote_average + " -  " + release_date ;
    }
}

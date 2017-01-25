package com.example.android.popularmovies.app.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 *
 *
 * Created by mhuertas on 9/11/16.
 */
public class Movie implements Parcelable {

    public static final String POPULAR_MOVIE = "popular";
    public static final String TOP_RATED_MOVIE = "top_rated";
    public static final String FAVOURITE_MOVIE = "favourite";

    private String id;
    private String title;
    private String backdrop_path;
    private String poster_path;
    private String overview;
    private String vote_average;
    private String release_date;
    private boolean favourite;
    private String criteria;

    /**
     * The Trailers for a movie. It is loaded LAZY.
     */
    private List<Trailer> trailers;

    /**
     * The Review for a movie. It is loaded LAZY.
     */
    private List<Review> reviews;

    public Movie() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
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

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String toString() {
        return  title + " - " + poster_path + " - " + vote_average + " -  " + release_date ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(backdrop_path);
        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeString(vote_average);
        dest.writeString(release_date);
        dest.writeByte((byte) (favourite ? 1 : 0));
        dest.writeString(criteria);
    }

    protected Movie(Parcel in) {
        id=in.readString();
        title = in.readString();
        backdrop_path = in.readString();
        poster_path = in.readString();
        overview = in.readString();
        vote_average = in.readString();
        release_date = in.readString();
        favourite = in.readByte() != 0;
        criteria = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}

package com.example.android.popularmovies.app.data;

/**
 * Created by mhuertas on 7/01/17.
 */

public class Trailer {

    private String id;
    private String key;
    private String name;

    public Trailer() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return  id + " - " +  " - " + key + " -  " +  " - " + name ;
    }

}

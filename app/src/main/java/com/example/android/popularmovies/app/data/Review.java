package com.example.android.popularmovies.app.data;

/**
 * Created by mhuertas on 9/01/17.
 */

public class Review {

    private String id;
    private String author;
    private String content;
    private String url;

    public Review() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String toString() {
        return  id + " - " + author + " - " + content.substring(0,10) + " -  " + url;
    }

}

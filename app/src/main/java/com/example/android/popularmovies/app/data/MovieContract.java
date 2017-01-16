package com.example.android.popularmovies.app.data;

import android.provider.BaseColumns;

/**
 * Created by mhuertas on 16/01/17.
 */
public class MovieContract {

    public static final class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "ID";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_POSTER_PATH = "poster_path";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_FAVOURITE = "favourite";
    }

    public static final class TrailerEntry implements BaseColumns {
        public static final String TABLE_NAME = "trailer";

        public static final String COLUMN_TRAILER_ID = "ID";
        public static final String COLUMN_TRAILER_KEY = "key";
        public static final String COLUMN_TRAILER_NAME = "name";
        public static final String COLUMN_TRAILER_MOVIE_ID = "movie_id";
    }

    public static final class ReviewEntry implements BaseColumns {
        public static final String TABLE_NAME = "review";

        public static final String COLUMN_REVIEW_ID = "ID";
        public static final String COLUMN_REVIEW_AUTHOR = "author";
        public static final String COLUMN_REVIEW_CONTENT = "content";
        public static final String COLUMN_REVIEW_URL = "url";
        public static final String COLUMN_REVIEW_MOVIE_ID = "movie_id";
    }

}
package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.android.popularmovies.app.data.MovieContract;

import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * Created by mhuertas on 16/01/17.
 */

public class TestUtilities {

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    public static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "Dancing in the rain");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, "Nice");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, "http://");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_FAVOURITE, "1");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, "1952");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, "8");
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 1);

        return movieValues;
    }

    public static ContentValues createReviewValues(long movideRowId) {
        ContentValues reviewValues = new ContentValues();

        reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, "Manuel Huertas");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, "Brillant");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL, "http://");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_MOVIE_ID, movideRowId);
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, 1);

        return reviewValues;
    }

    public static ContentValues createTrailerValues(long movideRowId) {
        ContentValues trailerValues = new ContentValues();

        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, "key");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, "name");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_MOVIE_ID, movideRowId);
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, 1);

        return trailerValues;
    }
}

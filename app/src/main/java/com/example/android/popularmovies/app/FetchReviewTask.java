package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.app.data.MovieContract;
import com.example.android.popularmovies.app.data.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mhuertas on 9/01/17.
 */

public class FetchReviewTask extends AsyncTask<String,Void,Void> {

    private final String LOG_TAG = FetchReviewTask.class.getSimpleName();

    /**
     *  Context to access resources.
     */
    private Context mContext;

    public FetchReviewTask(Context context) {
        this.mContext=context;
    }

    @Override
    protected Void doInBackground(String... params) {
        Uri builtUri = buildUri(params[0]);

        Log.v(LOG_TAG, "Reviews URL: " + builtUri.toString());
        try {
            URL url = new URL(builtUri.toString());
            Review[] reviews = parseResponce(Utility.request(url));
            insertDB(Integer.parseInt(params[0]),reviews);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return null;
    }

    /**
     * Build the URI for the Movie service API call.
     *
     * @param ID    The ID for the movie to get the reviews from.
     *
     * @return      The URI.
     */
    private Uri buildUri(String ID) {
        // Construct the URL
        final String APPID_PARAM = "api_key";

        Uri.Builder builder = new Uri.Builder();
        Uri builtUri = builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(ID)
                .appendPath("reviews")
                .appendQueryParameter(APPID_PARAM,BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();
        return builtUri;
    }

    /**
     * Parse the responce from the Movie API service.
     *
     * @param reviewsJsonStr    The responce in JSON.
     * @return                  A list of Review Objects.
     *
     * @throws JSONException    Error parsing the responce from serivce.
     */
    private Review[] parseResponce(String reviewsJsonStr) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String REVIEW_ID = "id";
        final String REVIEW_AUTHOR = "author";
        final String REVIEW_CONTENT = "content";
        final String REVIEW_URL = "url";
        final String REVIEW_LIST = "results";

        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
        JSONArray reviewsArray = reviewsJson.getJSONArray(REVIEW_LIST);
        Review[] results = new Review[reviewsArray.length()];
        for(int i = 0; i < reviewsArray.length(); i++) {
            // Get the JSON object representing a movie
            JSONObject reviewJson = reviewsArray.getJSONObject(i);

            //Build the movie entry string
            Review review = new Review();
            review.setId(reviewJson.getString(REVIEW_ID));
            review.setAuthor(reviewJson.getString(REVIEW_AUTHOR));
            review.setContent(reviewJson.getString(REVIEW_CONTENT));
            review.setUrl(reviewJson.getString(REVIEW_URL));

            results[i] = review;
        }

        for (Review s : results) {
            Log.v(LOG_TAG, "Trailer entry: " + s);
        }
        return results;
    }

    /**
     * Insert the movies Reviews into the database using a content provider.
     *
     * @param movieId   The movie id for the review.
     * @param reviews   A list of reviews
     */
    private void insertDB(Integer movieId,Review[] reviews) {
        ContentValues[] values = new ContentValues[reviews.length];
        int i=0;

        for (Review review : reviews) {
            ContentValues reviewValues = new ContentValues();

            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, review.getAuthor());
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL, review.getUrl());
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, review.getContent());
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, review.getId());
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_MOVIE_ID, movieId);
            values[i++]=reviewValues;
        }
        mContext.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI,values);
    }

}

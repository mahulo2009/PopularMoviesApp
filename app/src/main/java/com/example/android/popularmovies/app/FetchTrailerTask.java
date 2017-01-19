package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.android.popularmovies.app.data.MovieContract;
import com.example.android.popularmovies.app.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mhuertas on 7/01/17.
 */
public class FetchTrailerTask extends AsyncTask<String,Void,Void> {

    private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();
    private View mView;
    private Context mContext;
    private Callback mCallback;

    public FetchTrailerTask(Context context, View view,Callback callback) {
        this.mContext=context;
        mView=view;
        this.mCallback=callback;
    }

    @Override
    protected Void doInBackground(String... params) {
        Uri builtUri = buildUri(params[0]);

        Log.v(LOG_TAG, "Reviews URL: " + builtUri.toString());
        try {
            URL url = new URL(builtUri.toString());
            Trailer[] trailers = parseResponce(Utility.request(url));
            insertDB(Integer.parseInt(params[0]),trailers);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        mCallback.onTrailerInserted();
    }

    /**
    * Build the URI for the Movie service API call.
    *
    * @param ID    The ID for the movie to get the trailers from.
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
                .appendPath("videos")
                .appendQueryParameter(APPID_PARAM,BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();
        return builtUri;
    }

    /**
     * Parse the responce from the Movie API service.
     *
     * @param trailersJsonStr   The responce in JSON.
     * @return                  A list of Trailer Objects.
     *
     * @throws JSONException    Error parsing the responce from serivce.
     */
    private Trailer[] parseResponce(String trailersJsonStr) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String TRAILER_ID = "id";
        final String TRAILER_KEY = "key";
        final String TRAILER_NAME = "name";
        final String TRAILER_LIST = "results";

        JSONObject trailersJson = new JSONObject(trailersJsonStr);
        JSONArray trailersArray = trailersJson.getJSONArray(TRAILER_LIST);
        Trailer[] results = new Trailer[trailersArray.length()];
        for(int i = 0; i < trailersArray.length(); i++) {
            // Get the JSON object representing a movie
            JSONObject trailerJson = trailersArray.getJSONObject(i);

            //Build the movie entry string
            Trailer trailer = new Trailer();
            trailer.setId(trailerJson.getString(TRAILER_ID));
            trailer.setKey(trailerJson.getString(TRAILER_KEY));
            trailer.setName(trailerJson.getString(TRAILER_NAME));

            results[i] = trailer;
        }
        for (Trailer s : results) {
            Log.v(LOG_TAG, "Trailer entry: " + s);
        }
        return results;
    }

    /**
     * Insert the movies Trailer into the database using a content provider.
     *
     * @param movieId   The movie id for the trailer.
     * @param trailers   A list of trailers
     */
    private void insertDB(Integer movieId,Trailer[] trailers) {
        ContentValues[] values = new ContentValues[trailers.length];
        int i=0;
        for (Trailer trailer : trailers) {
            ContentValues trailerValues = new ContentValues();
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, trailer.getKey());
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, trailer.getName());
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_MOVIE_ID, movieId);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, trailer.getId());
            values[i++]=trailerValues;
        }
        mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI,values);
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onTrailerInserted();
    }

}

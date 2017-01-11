package com.example.android.popularmovies.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.example.android.popularmovies.app.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mhuertas on 7/01/17.
 */
public class FetchTrailerTask extends AsyncTask<Void,Void,Trailer[]> {

    private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();
    private View mView;

    public FetchTrailerTask(View view) {
        mView=view;
    }

    @Override
    protected Trailer[] doInBackground(Void... voids) {
        Uri builtUri = buildUri();

        Log.v(LOG_TAG, "Reviews URL: " + builtUri.toString());
        try {
            URL url = new URL(builtUri.toString());
            return parseResponce(Utility.request(url));
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Trailer[] result) {
        super.onPostExecute(result);
        if (result != null) {
            //TODO
        }
    }

    private Uri buildUri() {
        // Construct the URL
        final String APPID_PARAM = "api_key";

        Uri.Builder builder = new Uri.Builder();
        Uri builtUri = builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath("328111") //TODO THE MOVIE ID.
                .appendPath("videos")
                .appendQueryParameter(APPID_PARAM,BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();
        return builtUri;
    }

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

}

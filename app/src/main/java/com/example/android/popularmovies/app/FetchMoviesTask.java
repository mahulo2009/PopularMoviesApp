package com.example.android.popularmovies.app;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Async task to query the API in the background to obtain a list of movies. In the UI thread
 * the Array Adapter is updated.
 */
public class FetchMoviesTask extends AsyncTask<Void,Void,Movie[]> {

    private Context mContext;

    private ArrayAdapter<Movie> mMovieAdapter;

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();



    public FetchMoviesTask(Context Context,ArrayAdapter<Movie> movieAdapter) {
        this.mContext=Context;
        this.mMovieAdapter=movieAdapter;
    }


    /**
     * Build the complete image from the relative path.
     *
     * @param poster_path   relative image path
     *
     * @return              The complete image path
     */
    private String getImagePath(String poster_path)  {

        URI uri = null;
        try {
            uri = new URI("http","image.tmdb.org","/t/p/w185" + poster_path,null);
        } catch (URISyntaxException e) {
            return "";
        }
        return uri.toString();


    }

    /**
     * Build a list of Movie objects from the JSON string from the API request.
     *
     * @param movieJsonStr      JSON string from the API request.
     *
     * @return                  List of movie objects
     *
     * @throws JSONException    Exception JSON parsing
     */
    private Movie[] getMoviesDataFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String MV_TITLE = "original_title";
        final String MV_POSTER_PATH = "poster_path";
        final String MV_OVERVIEW = "overview";
        final String MV_VOTE_AVERAGE = "vote_average";
        final String MV_RELEASE_DATE = "release_date";
        final String MV_LIST = "results";

        //Parse the JSON result and get the movie list
        JSONObject moviesJson = new JSONObject(movieJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(MV_LIST);

        //Store the result into an array.
        Movie[] results = new Movie[moviesArray.length()];
        for(int i = 0; i < moviesArray.length(); i++) {
            // Get the JSON object representing a movie
            JSONObject movieJson = moviesArray.getJSONObject(i);

            //Build the movie entry string
            Movie movie = new Movie();
            movie.setTitle(movieJson.getString(MV_TITLE));
            movie.setPoster_path(getImagePath(movieJson.getString(MV_POSTER_PATH)));
            movie.setOverview(movieJson.getString(MV_OVERVIEW));
            movie.setVote_average(movieJson.getString(MV_VOTE_AVERAGE));
            movie.setRelease_date(movieJson.getString(MV_RELEASE_DATE));

            results[i] = movie;
        }

        for (Movie s : results) {
            Log.v(LOG_TAG, "Movie entry: " + s);
        }
        return results;
    }

    /**
     * Get from the preferences the order by criteria
     *
     * @return
     */
    private String getOrderBy() {
        String orderStr = PreferenceManager.getDefaultSharedPreferences(mContext).
                getString(mContext.getString(R.string.pref_sort_order_key),
                        mContext.getString(R.string.pref_most_popular_order));

        return orderStr;
    }

    @Override
    protected Movie[] doInBackground(Void... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        try {
            // Construct the URL
            final String APPID_PARAM = "api_key";

            Uri.Builder builder = new Uri.Builder();
            Uri builtUri = builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath(getOrderBy())
                    .appendQueryParameter(APPID_PARAM,BuildConfig.THE_MOVIE_DB_API_KEY)
                    .build();

            Log.v(LOG_TAG, "Movies URL: " + builtUri.toString());
            URL url = new URL(builtUri.toString());

            // Create the request to Themoviedb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            moviesJsonStr = buffer.toString();

            Log.v(LOG_TAG, "Movie JSON String: " + moviesJsonStr);

            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attemping
            // to parse it.
            return null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Movie[] result) {
        super.onPostExecute(result);

        if (result != null) {
            mMovieAdapter.clear();
            for (Movie m : result) {
                mMovieAdapter.add(m);
            }
        }
    }
}

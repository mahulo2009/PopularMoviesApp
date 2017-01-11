package com.example.android.popularmovies.app;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.android.popularmovies.app.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
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

    @Override
    protected Movie[] doInBackground(Void... params) {
        Uri builtUri = buildUri();
        Log.v(LOG_TAG, "Movies URL: " + builtUri.toString());

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
    protected void onPostExecute(Movie[] result) {
        super.onPostExecute(result);

        if (result != null) {
            mMovieAdapter.clear();
            for (Movie m : result) {
                mMovieAdapter.add(m);
            }
        }
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

    private Uri buildUri() {
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
        return builtUri;
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
    private Movie[] parseResponce(String movieJsonStr)
            throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String MV_ID = "id";
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
            movie.setId(movieJson.getString(MV_ID));
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

}

package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.app.data.Movie;
import com.example.android.popularmovies.app.data.MovieContract;

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
public class FetchMoviesTask extends AsyncTask<String,Void,Void> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    /**
     *  Context to access resources.
     */
    private Context mContext;

    /**
     * Constructor
     *
     * @param Context   Context to access resources.
     */
    public FetchMoviesTask(Context Context) {
        this.mContext=Context;
    }

    /**
     * Access the movie API in the background
     *
     * @param params    The query criteria: popular or top rated.
     *
     * @return          The List of movies
     */
    @Override
    protected Void doInBackground(String... params) {
        Uri builtUri = buildUri(params[0]);
        Log.v(LOG_TAG, "Movies URL: " + builtUri.toString());

        try {
            //Build the URI for the API Movie service
            URL url = new URL(builtUri.toString());
            //Parse the responce from json to java pojo.
            Movie[] movies = parseResponce(Utility.request(url));
            //Insert into the database.
            insertDB(movies,params[0]);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return null;
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
     * Build the URI to access the API movie service
     *
     * @param orderBy   Order by popular or top rated.
     * @return
     */
    private Uri buildUri(String orderBy) {
        // Construct the URL
        final String APPID_PARAM = "api_key";

        Uri.Builder builder = new Uri.Builder();
        Uri builtUri = builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(orderBy)
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

    /**
     * Insert into the database, table movie, the result.
     *
     * @param movies    The list of movies
     * @param orderBy   The order criteria.
     *
     */
    private void insertDB(Movie[] movies,String orderBy) {
        ContentValues[] values = new ContentValues[movies.length];
        int i=0;
        for (Movie movie : movies) {
            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, movie.getPoster_path());
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_FAVOURITE, movie.isFavourite());
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getRelease_date());
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, movie.getVote_average());
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_CRITERIA,orderBy );
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());

            values[i++]=movieValues;
        }
        mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI,values);
    }

}

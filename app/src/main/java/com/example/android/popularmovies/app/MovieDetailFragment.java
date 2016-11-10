package com.example.android.popularmovies.app;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    private TextView mTextView;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_deatil, container, false);

        mTextView = (TextView)rootView.findViewById(R.id.detail_text);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String mMovieId = intent.getStringExtra(Intent.EXTRA_TEXT);


            FetchMovieDetailTask fetchMovieDetailTask = new FetchMovieDetailTask();
            fetchMovieDetailTask.execute(mMovieId);
        }

        return rootView;
    }

    /**
     * Async task to query the API in the background to obtain the deails of a movie.
     */
    public class FetchMovieDetailTask extends AsyncTask<String,Void,Movie> {

        private final String LOG_TAG = MovieDetailFragment.FetchMovieDetailTask.class.getSimpleName();

        private Movie getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MV_ID = "id";
            final String MV_TITLE = "original_title";
            final String MV_POSTER_PATH = "poster_path";
            final String MV_OVERVIEW = "overview";
            final String MV_VOTE_AVERAGE = "vote_average";
            final String MV_RELEASE_DATE = "release_date";

            //Parse the JSON result and get the movie list
            JSONObject moviesJson = new JSONObject(movieJsonStr);

            //Build the movie from json
            Movie movie = new Movie();
            movie.setId(moviesJson.getInt(MV_ID));
            movie.setTitle(moviesJson.getString(MV_TITLE));
            movie.setPoster_path(moviesJson.getString(MV_POSTER_PATH));
            movie.setOverview(moviesJson.getString(MV_OVERVIEW));
            movie.setVote_average(moviesJson.getString(MV_VOTE_AVERAGE));
            movie.setRelease_date(moviesJson.getString(MV_RELEASE_DATE));

            Log.v(LOG_TAG, "Movie entry: " + movie);

            return movie;
        }


        @Override
        protected Movie doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                // Construct the URL
                final String APPID_PARAM = "api_key";


                Uri.Builder builder = new Uri.Builder();
                Uri builtUri = builder.scheme("https")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(params[0])
                        .appendQueryParameter(APPID_PARAM,BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                Log.v(LOG_TAG, "Movie URL: " + builtUri.toString());
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
                movieJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Movie JSON String: " + movieJsonStr);

                try {
                    return getMovieDataFromJson(movieJsonStr);
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
        protected void onPostExecute(Movie movie) {
            super.onPostExecute(movie);

            if (movie != null) {
                mTextView.setText(movie.toString());
            }
        }
    }

}

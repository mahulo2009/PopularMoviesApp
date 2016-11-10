package com.example.android.popularmovies.app;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Fragment to present a grid arrangement of movies posters
 */
public class MainFragment extends Fragment {
    /**
     * The movies array adapter
     */
    private ArrayAdapter<Movie> mMovieAdapter;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This makes the options menu available.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_refresh)
        {
            //When the refresh menu item is pressed we make a query, on the background
            //to the movie API and update the Array adapter.
            updateMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Creates the array update, empty.
        mMovieAdapter = new ArrayAdapter<Movie>(getActivity(),
                R.layout.list_item_movie,
                R.id.list_item_movie_textview,
                new ArrayList<Movie>());

        //Inflate the fragment.
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Create a list view to present the list of movies
        ListView listView = (ListView)rootView.findViewById(R.id.listview_movies);
        listView.setAdapter(mMovieAdapter);
        //If the user tap a movie entry, a detail view is showed
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mMovieAdapter.getItem(position);
                Intent detailIntent = new Intent(getActivity(),MovieDeatilActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT,Integer.toString(movie.getId())); //TODO TEXT OF INTEGER
                startActivity(detailIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }


    public void updateMovies() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute();
    }

    /**
     * Async task to query the API in the background to obtain a list of movies. In the UI thread
     * the Array Adapter is updated.
     */
    public class FetchMoviesTask extends AsyncTask<Void,Void,Movie[]>  {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();


        private Movie[] getMoviesDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MV_LIST = "results";
            final String MV_ID = "id";
            final String MV_POSTER_PATH = "poster_path";
            final String MV_TITLE = "original_title";

            //Parse the JSON result and get the movie list
            JSONObject moviesJson = new JSONObject(movieJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(MV_LIST);

            //Store the result into an array.
            Movie[] results = new Movie[moviesArray.length()];
            for(int i = 0; i < moviesArray.length(); i++) {

                int id;
                String poster_path;
                String title;

                // Get the JSON object representing a movie
                JSONObject movieJson = moviesArray.getJSONObject(i);

                id = movieJson.getInt(MV_ID);
                poster_path = movieJson.getString(MV_POSTER_PATH);
                title = movieJson.getString(MV_TITLE);

                //Build the movie entry string
                Movie movie = new Movie();
                movie.setId(id);
                movie.setTitle(title);
                movie.setPoster_path(poster_path);

                results[i] = movie;
            }

            for (Movie s : results) {
                Log.v(LOG_TAG, "Movie entry: " + s);
            }
            return results;
        }

        private String getOrderBy() {
            String orderStr = PreferenceManager.getDefaultSharedPreferences(getActivity()).
                    getString(getString(R.string.pref_sort_order_key),
                            getString(R.string.pref_most_popular_order));

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

}

package com.example.android.popularmovies.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
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
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Fragment to present a grid arrangement of movies posters
 */
public class MainFragment extends Fragment {

    private final String LOG_TAG = MainFragment.class.getSimpleName();

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
        mMovieAdapter = new MovieAdapter(getActivity(),
                                            new ArrayList<Movie>());

        //Inflate the fragment.
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Create a list view to present the list of movies
        GridView gridView = (GridView)rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMovieAdapter);
        //If the user tap a movie entry, a detail view is showed
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mMovieAdapter.getItem(position);

                Intent detailIntent = new Intent(getActivity(),MovieDeatilActivity.class);
                detailIntent.putExtra(MovieDetailFragment.MOVIE_KEY,movie);
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
        if (isOnline()) {
            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getContext(),mMovieAdapter);
            fetchMoviesTask.execute();
        } else  {
            Toast.makeText(getActivity(), "Check your connection and try again", Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG,"NOT internet connectivity for the moment");
        }
    }

    /**
     * Checks the device is connected to Internet.
     *
     * @return
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}

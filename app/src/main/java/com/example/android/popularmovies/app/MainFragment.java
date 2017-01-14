package com.example.android.popularmovies.app;

import android.content.Intent;
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
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popularmovies.app.data.Movie;

import java.util.ArrayList;

/**
 * Fragment to present a grid arrangement of movies posters
 */
public class MainFragment extends Fragment {

    private final String LOG_TAG = MainFragment.class.getSimpleName();

    public static final String ORDER_BY_KEY = "ORDER_BY_KEY";
    /**
     * The movies array adapter
     */
    private MovieAdapter mMovieAdapter;
    private String mOrderBy;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This makes the options menu available.
        setHasOptionsMenu(true);
        //The order by criteria: top rated or popular movies.
        mOrderBy= getArguments().getString(ORDER_BY_KEY);
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

    /**
     * Fetch the movie data from API movie service.
     */
    public void updateMovies() {
        if (Utility.isOnline(getActivity())) {
            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getContext(),mMovieAdapter);
            fetchMoviesTask.execute(mOrderBy);
        } else  {
            Toast.makeText(getActivity(), "Check your connection and try again", Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG,"NOT internet connectivity for the moment");
        }
    }

}
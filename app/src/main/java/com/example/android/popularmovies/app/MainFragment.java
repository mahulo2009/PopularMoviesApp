package com.example.android.popularmovies.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.example.android.popularmovies.app.data.MovieContract;

/**
 * Fragment to present a grid arrangement of movies posters
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private final String LOG_TAG = MainFragment.class.getSimpleName();

    /**
     * The ID for the Loader.
     */
    private static final int MOVIE_LOADER=0 ;


    public static final String ORDER_BY_KEY = "ORDER_BY_KEY";
    /**
     * The movies array adapter
     */
    private MovieAdapter mMovieAdapter;

    /**
     * Criteria: popular, top rated, favourites.
     */
    private String mOrderBy;

    /**
     *  The column to extract for the query to the content provider.
     */
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH
    };
    static final int COLUMN_MOVIE_ID=1;
    static final int COLUMN_MOVIE_POSTER_PATH=2;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        //Create the Loader in the background.
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Creates the array update, empty.
        mMovieAdapter = new MovieAdapter(getActivity(), null,0);

        //Inflate the fragment.
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Create a list view to present the list of movies
        GridView gridView = (GridView)rootView.findViewById(R.id.gridview_movies);
        gridView.setAdapter(mMovieAdapter);
        //If the user tap a movie entry, a detail view is showed
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    //Create the URI for the detail view and start activity.
                    Uri uri = MovieContract.MovieEntry.buildMovieUriAPIId(cursor.getString(COLUMN_MOVIE_ID));
                    Intent detailIntent =
                            new Intent(getActivity(),MovieDeatilActivity.class).setData(uri);
                    startActivity(detailIntent);
                }
            }
        });
        return rootView;
    }

    /**
     * Fetch the movie data from API movie service.
     */
    //TODO This method will became obsoleto when the sync manger used.
    public void updateMovies() {
        if (Utility.isOnline(getActivity())) {
            //If the order by is not Favourite, request to internet.
            if (!mOrderBy.equals(Movie.FAVOURITE_MOVIE)) {
                //If there is internet connection make the API request in the background.
                FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getContext());
                fetchMoviesTask.execute(mOrderBy);
            }
        } else  {
            Toast.makeText(getActivity(), "Check your connection and try again", Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG,"NOT internet connectivity for the moment");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //TODO: This can be a type of query to the provider.
        String sMovieCriteriaSelection =
                MovieContract.MovieEntry.TABLE_NAME+
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_CRITERIA + " = ? ";
        String[]  selectionArgs = new String[]{mOrderBy};

        //Build the URI.
        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                movieUri,
                MOVIE_COLUMNS,
                sMovieCriteriaSelection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }

}
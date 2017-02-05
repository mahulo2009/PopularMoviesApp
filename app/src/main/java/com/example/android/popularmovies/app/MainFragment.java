package com.example.android.popularmovies.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

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
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
            MovieContract.MovieFavoriteEntry.COLUMN_FAVORITE_MOVIE_ID
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
    public void onActivityCreated(Bundle savedInstanceState) {
        //Create the Loader in the background.
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().restartLoader(MOVIE_LOADER,null,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Creates the array update, empty.
        mMovieAdapter = new MovieAdapter(getActivity());

        //Inflate the fragment.
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Create a list view to present the list of movies
        RecyclerView gridView = (RecyclerView)rootView.findViewById(R.id.gridview_movies);
        gridView.setHasFixedSize(true);
        GridLayoutManager layout = new GridLayoutManager(getContext(),2);
        gridView.setLayoutManager(layout);
        gridView.setAdapter(mMovieAdapter);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //TODO: This can be a type of query to the provider.
        String sMovieCriteriaSelection;
        String[] selectionArgs = null;
        if (!mOrderBy.equals(Movie.FAVOURITE_MOVIE)) {
            sMovieCriteriaSelection =
                    MovieContract.MovieEntry.TABLE_NAME +
                            "." + MovieContract.MovieEntry.COLUMN_MOVIE_CRITERIA + " = ? ";
            selectionArgs = new String[]{mOrderBy};
        } else {
            sMovieCriteriaSelection =
                             MovieContract.MovieFavoriteEntry.COLUMN_FAVORITE_MOVIE_ID + " is not null ";
        }
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
package com.example.android.popularmovies.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.popularmovies.app.data.MovieContract;

/**
 * Created by mhuertas on 23/01/17.
 */
public class TrailerFragment  extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = TrailerFragment.class.getSimpleName();
    /**
     * The ID for the TRAILER LOADER
     */
    private static final int TRAILER_LOADER = 1;

    private static final String[] TRAILER_COLUMNS = {
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_TRAILER_ID,
            MovieContract.TrailerEntry.COLUMN_TRAILER_KEY,
            MovieContract.TrailerEntry.COLUMN_TRAILER_NAME,
            MovieContract.TrailerEntry.COLUMN_TRAILER_MOVIE_ID
    };
    public static final int COLUMN_TRAILER_KEY = 2;
    public static final int COLUMN_TRAILER_NAME = 3;
    private Uri mUri;

    /**
     * The movies array adapter
     */
    private TrailerAdapter mTrailerAdapter;

    public TrailerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Creates the array update, empty.
        mTrailerAdapter = new TrailerAdapter(getActivity(), null,0);
        //Inflate the fragment.
        View rootView = inflater.inflate(R.layout.fragment_movie_trailer, container, false);
        //Read the URI for the movie.
        mUri = getActivity().getIntent().getData();

        //Create a list view to present the list of movies
        ListView listView = (ListView)rootView.findViewById(R.id.listview_trailers);
        listView.setAdapter(mTrailerAdapter);
        //If the user tap a movie entry, a detail view is showed
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    //TODO GO TO WEB PAGE.
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            String movieId = MovieContract.MovieEntry.getMovieIdFromUri(mUri);
            Uri uri = MovieContract.TrailerEntry.buildTrailerUri(Integer.parseInt(movieId));
            return new CursorLoader(
                    getActivity(),
                    uri,
                    TRAILER_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mTrailerAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTrailerAdapter.swapCursor(null);
    }

}

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

public class ReviewFragment extends Fragment  implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = ReviewFragment.class.getSimpleName();
    /**
     * The movies array adapter
     */
    private ReviewAdapter mReviewAdapter;
    /**
     * The ID for the REVIEW LOADER
     */
    private static final int REVIEW_LOADER = 2;

    private static final String[] REVIEW_COLUMNS = {
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_REVIEW_ID,
            MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT,
            MovieContract.ReviewEntry.COLUMN_REVIEW_URL
    };
    public static final int COLUMN_REVIEW_AUTHOR = 2;
    public static final int COLUMN_REVIEW_CONTENT = 3;
    public static final int COLUMN_REVIEW_URL = 4;

    private Uri mUri;

    public ReviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Creates the array update, empty.
        mReviewAdapter = new ReviewAdapter(getActivity(), null,0);
        //Inflate the fragment.
        View rootView = inflater.inflate(R.layout.fragment_movie_review, container, false);
        //Read the URI for the movie.
        mUri = getActivity().getIntent().getData();
        //Create a list view to present the list of movies
        ListView listView = (ListView)rootView.findViewById(R.id.listview_reviews);
        listView.setAdapter(mReviewAdapter);
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
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            String movieId = MovieContract.MovieEntry.getMovieIdFromUri(mUri);
            Uri uri = MovieContract.ReviewEntry.buildReviewUri(Integer.parseInt(movieId));

            return new CursorLoader(
                    getActivity(),
                    uri,
                    REVIEW_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mReviewAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mReviewAdapter.swapCursor(null);
    }

}

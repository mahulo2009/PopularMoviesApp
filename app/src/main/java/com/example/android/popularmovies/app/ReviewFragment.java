package com.example.android.popularmovies.app;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        mReviewAdapter = new ReviewAdapter(getActivity());
        //Inflate the fragment.
        View rootView = inflater.inflate(R.layout.fragment_movie_review, container, false);
        //Read the URI for the movie.
        mUri = getActivity().getIntent().getData();
        //Create a list view to present the list of movies
        RecyclerView listView = (RecyclerView)rootView.findViewById(R.id.listview_reviews);
        listView.setHasFixedSize(true);
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        listView.setLayoutManager(layout);
        listView.setAdapter(mReviewAdapter);

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

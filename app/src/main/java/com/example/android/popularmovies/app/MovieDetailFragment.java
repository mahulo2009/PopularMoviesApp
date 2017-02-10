package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.app.data.Movie;
import com.example.android.popularmovies.app.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment implements
        FetchTrailerTask.Callback , LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    public static final String DETAIL_URI_KEY = "DETAIL_URI_KEY";

    private boolean mIsFavorite;

    /**
     * The ID for the DETAIL LOADER
     */
    private static final int DETAIL_LOADER = 0;
    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieFavoriteEntry.COLUMN_FAVORITE_MOVIE_ID,

    };
    public static final int COL_MOVIE_ID = 0;
    public static final int COLUMN_MOVIE_TITLE = 1;
    public static final int COLUMN_MOVIE_BACKDROP_PATH = 2;
    public static final int COLUMN_MOVIE_RELEASE_DATE = 3;
    public static final int COLUMN_MOVIE_VOTE_AVERAGE = 4;
    public static final int COLUMN_MOVIE_OVERVIEW = 5;
    public static final int COL_MOVIE_API_ID = 6;
    public static final int COLUMN_MOVIE_FAVORITE = 7;



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

    //The GUI elements
    private TextView mTitleTextView;
    private TextView mReleaseDateTextView;
    private TextView mRatingTextView;
    private TextView mOverViewTextView;
    private ImageView mImageView;
    private ImageButton mImageButton;

    /**
     * The URI for the movie.
     */
    private Uri mUri;
    /**
     * The movies array adapter
     */
    private TrailerAdapter mTrailerAdapter;
    /**
     * The movies array adapter
     */
    private ReviewAdapter mReviewAdapter;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (id == R.id.action_refresh)  {
            updateTrailers();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Adapter for the trailers
        mTrailerAdapter = new TrailerAdapter(getActivity());
        //Adapter for the reviews
        mReviewAdapter = new ReviewAdapter(getActivity());
        //Inflate
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        //Get the URI from the arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI_KEY);
        } else {
            //If the mUri is null, we query the first movie with the popular criteria
            //to update in the case of master/detail view the view.
            String sMovieSelection =
                    MovieContract.MovieEntry.TABLE_NAME+
                            "." + MovieContract.MovieEntry.COLUMN_MOVIE_CRITERIA + " = ? ";
            Cursor c =  getContext().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,DETAIL_COLUMNS,
                    sMovieSelection,
                    new String[]{Movie.POPULAR_MOVIE},
                    null);
            c.moveToFirst();
            if (c != null)
                mUri = MovieContract.MovieEntry.buildMovieUriAPIId(c.getString(COL_MOVIE_API_ID));
        }
        //Recycler view for the Trailers
        RecyclerView listView = (RecyclerView)rootView.findViewById(R.id.listview_trailers);
        listView.setHasFixedSize(true);
        LinearLayoutManager layout = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        listView.setLayoutManager(layout);
        listView.setAdapter(mTrailerAdapter);
        //Recycler view for the reviews
        RecyclerView listView1 = (RecyclerView)rootView.findViewById(R.id.listview_reviews);
        LinearLayoutManager layout1 = new LinearLayoutManager(getContext());
        listView1.setLayoutManager(layout1);
        listView1.setAdapter(mReviewAdapter);

        //Create the UI elements
        mTitleTextView = (TextView)rootView.findViewById(R.id.movie_title_textview);
        mReleaseDateTextView = (TextView)rootView.findViewById(R.id.movie_releasedate_textview);
        mRatingTextView = (TextView)rootView.findViewById(R.id.movie_user_rating_textview);
        mOverViewTextView = (TextView)rootView.findViewById(R.id.movie_user_overview_textview);
        mImageView = (ImageView)rootView.findViewById(R.id.movie_poster_imageview);
        mImageButton=(ImageButton)rootView.findViewById(R.id.favorite_button);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mUri) {
                    if (mIsFavorite) {
                        //TODO INCLUDE IN PROVIDER QUERY.
                        mIsFavorite = false;
                        //Remove from the favourite table
                        String sMovieSelection =
                                MovieContract.MovieFavoriteEntry.TABLE_NAME +
                                        "." + MovieContract.MovieFavoriteEntry.COLUMN_FAVORITE_MOVIE_ID + " = ? ";
                        String[] selectionArgs = new String[]{MovieContract.MovieEntry.getMovieIdFromUri(mUri)};
                        getContext().getContentResolver().delete(MovieContract.MovieFavoriteEntry.CONTENT_URI, sMovieSelection, selectionArgs);
                        //Update UI
                        updateFavouriteButton();
                    } else {
                        mIsFavorite = true;
                        //Add to the favourite table
                        ContentValues values = new ContentValues();
                        values.put(MovieContract.MovieFavoriteEntry.COLUMN_FAVORITE_MOVIE_ID, MovieContract.MovieEntry.getMovieIdFromUri(mUri));
                        getContext().getContentResolver().insert(MovieContract.MovieFavoriteEntry.CONTENT_URI, values);
                        //Update UI
                        updateFavouriteButton();
                    }
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTrailers();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            switch (id) {
                case DETAIL_LOADER: {
                    return new CursorLoader(
                            getActivity(),
                            mUri,
                            DETAIL_COLUMNS,
                            null,
                            null,
                            null
                    );
                }
                case TRAILER_LOADER: {
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
                case REVIEW_LOADER: {
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
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case DETAIL_LOADER: {
                if (data != null && data.moveToFirst()) {
                    mTitleTextView.setText(data.getString(COLUMN_MOVIE_TITLE));
                    mReleaseDateTextView.setText(Utility.extractYear(data.getString(COLUMN_MOVIE_RELEASE_DATE)));
                    mRatingTextView.setText(Utility.appendRating(data.getString(COLUMN_MOVIE_VOTE_AVERAGE)));
                    mOverViewTextView.setText(data.getString(COLUMN_MOVIE_OVERVIEW));
                    Picasso.with(getContext()).load(data.getString(COLUMN_MOVIE_BACKDROP_PATH)).into(mImageView);
                    mIsFavorite = data.getString(COLUMN_MOVIE_FAVORITE) == null ? false: true;
                    updateFavouriteButton();
                }
                break;
            }
            case TRAILER_LOADER: {
                mTrailerAdapter.swapCursor(data);
                break;
            }
            case REVIEW_LOADER: {
                mReviewAdapter.swapCursor(data);
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case DETAIL_LOADER: {

            }
            case TRAILER_LOADER: {
                mTrailerAdapter.swapCursor(null);
            }
            case REVIEW_LOADER: {
                mReviewAdapter.swapCursor(null);
            }
        }
    }

    private void updateFavouriteButton() {
        if (mIsFavorite) {
            mImageButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.btn_star_big_on));
        } else {
            mImageButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.btn_star_big_off));
        }
    }

    private void updateTrailers() {
        if ( null != mUri ) {
            String movieId = MovieContract.MovieEntry.getMovieIdFromUri(mUri);
            if (Utility.isOnline(getActivity())) {
                FetchTrailerTask fetchTrailerTask = new FetchTrailerTask(getContext(), getView().getRootView(), this);
                fetchTrailerTask.execute(movieId);
            } else {
                Toast.makeText(getActivity(), "Check your connection and try again", Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "NOT internet connectivity for the moment");
            }
        }
    }

    @Override
    public void onTrailerInserted() {
        updateReviews();
    }

    /**
     * Fetch the movie reviews from API movie service.
     *
     * The adapter will inflate the card views for each review.
     */
    private void updateReviews() {
        if ( null != mUri ) {
            String movieId = MovieContract.MovieEntry.getMovieIdFromUri(mUri);
            if (Utility.isOnline(getActivity())) {
                FetchReviewTask fetchreviewTask = new FetchReviewTask(getContext());
                fetchreviewTask.execute(movieId);
            } else {
                Toast.makeText(getActivity(), "Check your connection and try again", Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "NOT internet connectivity for the moment");
            }
        }
    }

}
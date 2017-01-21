package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.view.ContextThemeWrapper;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.app.data.MovieContract;
import com.squareup.picasso.Picasso;

import static com.example.android.popularmovies.app.Utility.buildImageFirstFotogram;
import static com.example.android.popularmovies.app.Utility.buildUriTrailer;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment implements
        FetchTrailerTask.Callback , LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    //TODO The number of words depending on layout
    private static int FIRST_N_WORDS = 15;

    /**
     * The ID for the DETAIL LOADER
     */
    private static final int DETAIL_LOADER = 0;
    /**
     * The ID for the TRAILER LOADER
     */
    private static final int TRAILER_LOADER = 1;
    /**
     * The ID for the REVIEW LOADER
     */
    private static final int REVIEW_LOADER = 2;

    private boolean mIsFavorite;

    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
            MovieContract.MovieFavoriteEntry.COLUMN_FAVORITE_MOVIE_ID
    };
    public static final int COL_MOVIE_ID = 0;
    public static final int COLUMN_MOVIE_TITLE = 1;
    public static final int COLUMN_MOVIE_POSTER_PATH = 2;
    public static final int COLUMN_MOVIE_RELEASE_DATE = 3;
    public static final int COLUMN_MOVIE_VOTE_AVERAGE = 4;
    public static final int COLUMN_MOVIE_OVERVIEW = 5;
    public static final int COLUMN_MOVIE_FAVORITE = 6;

    private static final String[] TRAILER_COLUMNS = {
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_TRAILER_ID,
            MovieContract.TrailerEntry.COLUMN_TRAILER_KEY,
            MovieContract.TrailerEntry.COLUMN_TRAILER_NAME,
            MovieContract.TrailerEntry.COLUMN_TRAILER_MOVIE_ID
    };
    public static final int COLUMN_TRAILER_KEY = 2;

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

    public MovieDetailFragment() {
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
        //Inflate
        View rootView = inflater.inflate(R.layout.fragment_movie_deatil, container, false);
        //Read the URI for the movie.
        mUri = getActivity().getIntent().getData();
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case DETAIL_LOADER: {
                if ( null != mUri ) {
                    return new CursorLoader(
                            getActivity(),
                            mUri,
                            DETAIL_COLUMNS,
                            null,
                            null,
                            null
                    );
                }
                break;
            }
            case TRAILER_LOADER: {
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
                break;
            }
            case REVIEW_LOADER: {
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
                break;
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch(loader.getId()) {
            case DETAIL_LOADER: {
                if (data != null && data.moveToFirst()) {
                    mTitleTextView.setText(data.getString(COLUMN_MOVIE_TITLE));
                    mReleaseDateTextView.setText(Utility.extractYear(data.getString(COLUMN_MOVIE_RELEASE_DATE)));
                    mRatingTextView.setText(Utility.appendRating(data.getString(COLUMN_MOVIE_VOTE_AVERAGE)));
                    mOverViewTextView.setText(data.getString(COLUMN_MOVIE_OVERVIEW));
                    Picasso.with(getContext()).load(data.getString(COLUMN_MOVIE_POSTER_PATH)).into(mImageView);
                    mIsFavorite = data.getString(COLUMN_MOVIE_FAVORITE) == null ? false: true;
                    updateFavouriteButton();
                }
                break;
            }
            case TRAILER_LOADER: {
                if (data != null && data.moveToFirst()) {
                    LinearLayout layout = (LinearLayout)getActivity().findViewById(R.id.trailer_linear_layout);
                    layout.removeAllViews();
                    while (data.moveToNext()) {
                        buildTrailerCard(data,layout);
                    }
                }
                break;
            }
            case REVIEW_LOADER: {
                if (data != null && data.moveToFirst()) {
                    LinearLayout layout = (LinearLayout)getActivity().findViewById(R.id.review_linear_layout);
                    layout.removeAllViews();
                    while (data.moveToNext()) {
                        buildReviewCard(data,layout);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void updateFavouriteButton() {
        if (mIsFavorite) {
            mImageButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.btn_star_big_on));
        } else {
            mImageButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), android.R.drawable.btn_star_big_off));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTrailers();
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
        getLoaderManager().restartLoader(REVIEW_LOADER,null,this);
    }

    //TODO Move to a CursorAdapter
    private void buildTrailerCard(Cursor cursor,LinearLayout layout) {
        int padding = Utility.dpToPx(getContext(),5);
        ImageView iv_trailer = new ImageView(getContext());
        iv_trailer.setTag(cursor.getString(COLUMN_TRAILER_KEY));
        iv_trailer.setPadding(padding ,padding ,padding ,padding );
        iv_trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = (String) v.getTag();
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        buildUriTrailer(key));
                webIntent.putExtra("force_fullscreen",true);
                getContext().startActivity(webIntent);
            }
        });
        Picasso.with(getContext()).load(buildImageFirstFotogram(cursor.getString(COLUMN_TRAILER_KEY))).into(iv_trailer);
        layout.addView(iv_trailer);
    }

    //TODO Move to a CursorAdapter
    private void buildReviewCard(Cursor cursor,LinearLayout layout) {
        TextView tv_title = new TextView(new ContextThemeWrapper(getContext(), R.style.CardViewTextStyle));
        tv_title.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tv_title.setText("Review by " + cursor.getString(COLUMN_REVIEW_AUTHOR)); //TODO String in other file.
        layout.addView(tv_title);

        TextView tv_overview = new TextView(new ContextThemeWrapper(getContext(), R.style.CardViewTextStyle));
        tv_overview.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tv_overview.setText(Utility.getFirstNStrings(cursor.getString(COLUMN_REVIEW_CONTENT), FIRST_N_WORDS));
        layout.addView(tv_overview);

        //TODO For the moment it shows the URL, it will be possible to use a fragment to show the
        //complete review.
        TextView tv_url = new TextView(new ContextThemeWrapper(getContext(), R.style.CardViewTextStyle));
        tv_url.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tv_url.setText(Utility.buildUrlReadMore(cursor.getString(COLUMN_REVIEW_URL)));
        tv_url.setMovementMethod(LinkMovementMethod.getInstance());
        layout.addView(tv_url);
    }

}

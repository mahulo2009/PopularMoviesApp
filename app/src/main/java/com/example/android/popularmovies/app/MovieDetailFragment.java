package com.example.android.popularmovies.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.app.data.Movie;
import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    /**
     * The key to extract the movie object from the intent
     */
    public static String MOVIE_KEY="MOVIE_KEY";
    /**
     * The movie actually represented
     */
    private Movie mMovie;

    //The GUI elements
    private TextView mTitleTextView;
    private TextView mReleaseDateTextView;
    private TextView mRatingTextView;
    private TextView mOverViewTextView;
    private ImageView mImageView;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_deatil, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(MOVIE_KEY)) {
            //Extract from the intent the Parcelable movie object.
            mMovie = (Movie)intent.getParcelableExtra(MOVIE_KEY);

            mTitleTextView = (TextView)rootView.findViewById(R.id.movie_title_textview);
            mTitleTextView.setText(mMovie.getTitle());

            mReleaseDateTextView = (TextView)rootView.findViewById(R.id.movie_releasedate_textview);
            mReleaseDateTextView.setText(Utility.extractYear(mMovie.getRelease_date()));

            mRatingTextView = (TextView)rootView.findViewById(R.id.movie_user_rating_textview);
            mRatingTextView.setText(Utility.appendRating(mMovie.getVote_average()));

            mOverViewTextView = (TextView)rootView.findViewById(R.id.movie_user_overview_textview);
            mOverViewTextView.setText(mMovie.getOverview());

            mImageView = (ImageView)rootView.findViewById(R.id.movie_poster_imageview);
            Picasso.with(getContext()).load(mMovie.getPoster_path()).into(mImageView);
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateReviews();
    }

    /**
     * Fetch the movie reviews from API movie service.
     *
     * The adapter will inflate the card views for each review.
     */
    private void updateReviews() {
        if (Utility.isOnline(getActivity())) {
            if (mMovie!=null) {
                FetchReviewTask fetchreviewTask = new FetchReviewTask(getContext(),getView().getRootView());
                fetchreviewTask.execute(mMovie.getId());
            }
        } else  {
            Toast.makeText(getActivity(), "Check your connection and try again", Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG,"NOT internet connectivity for the moment");
        }
    }

    private void updateTrailers() {
        if (Utility.isOnline(getActivity())) {
            FetchTrailerTask fetchTrailerTask = new FetchTrailerTask(getView().getRootView());
            fetchTrailerTask.execute();
        } else  {
            Toast.makeText(getActivity(), "Check your connection and try again", Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG,"NOT internet connectivity for the moment");
        }
    }

}

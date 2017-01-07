package com.example.android.popularmovies.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {

    public static String MOVIE_KEY="MOVIE";


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
            Movie movie = (Movie)intent.getParcelableExtra(MOVIE_KEY);


            mTitleTextView = (TextView)rootView.findViewById(R.id.movie_title_textview);
            mTitleTextView.setText(movie.getTitle());

            mReleaseDateTextView = (TextView)rootView.findViewById(R.id.movie_releasedate_textview);
            mReleaseDateTextView.setText(extractYear(movie.getRelease_date()));

            mRatingTextView = (TextView)rootView.findViewById(R.id.movie_user_rating_textview);
            mRatingTextView.setText(appendRating(movie.getVote_average()));

            mOverViewTextView = (TextView)rootView.findViewById(R.id.movie_user_overview_textview);
            mOverViewTextView.setText(movie.getOverview());

            mImageView = (ImageView)rootView.findViewById(R.id.movie_poster_imageview);
            Picasso.with(getContext()).load(movie.getPoster_path()).into(mImageView);
        }

        return rootView;
    }


    //TODO IMPROVE THIS PART.

    /**
     * Extract the year string from the data
     *
     * @param date  date string YY-mm-dd
     *
     * @return      Year string or empty if a problem.
     */
    private String extractYear(String date) {
        String[] tokens = date.split("-");
        if (tokens.length!=0) {
            return tokens[0];
        }
        return "";
    }

    /**
     * Append the max rating to the rate string
     *
     * @param rate      rate string
     *
     * @return          rate + /10
     */
    private String appendRating(String rate) {
        return rate+"/10";
    }

}

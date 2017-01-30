package com.example.android.popularmovies.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.facebook.stetho.Stetho;


/**
 * The main activity presents a grid arrangement of movies posters.
 *
 * There are three tabs to see the movie with the following criterias:
 * - Top Rated: As rated from the API movie service
 * - Most Popular: Idem.
 * - Favourites: To show movies marked for the user as favourites. These movies are
 * stored locally.
 *
 * A detail screen is showed, with additional information, when the user tap on a movie.
 */
public class MainActivity extends AppCompatActivity implements MovieAdapter.Callback {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Boolean to indicate the layout strategy depending on device size.
     */
    private boolean mTwoPane;

    public static final String DETAILFRAGMENT_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Add Stetho debug.
        //TODO REMOVE FOR PRODUCTION
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);

        //Creates the main fragment with the grid arrangement of movies posters
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view);
        setSupportActionBar(toolbar);

        MovieViewPagerAdapter adapter = new MovieViewPagerAdapter(getSupportFragmentManager(),getBaseContext());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if (findViewById(R.id.fragment_detail) != null) {
            //If this element exits in the layout, in the two pane configuration.
            mTwoPane=true;
            if (savedInstanceState == null) {
                //Replace the layout element with the fragment detail view.
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_detail, new MovieDetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane=false;
        }
    }

    /**
     * Call Back after selecting an element in the grid (a movie) to
     * show the details.
     *
     * The details can be saw in a ne
     *
     * @param uri   URI for the movie.
     */
    @Override
    public void onItemSelected(Uri uri) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.DETAIL_URI_KEY, uri);
            MovieDetailFragment fragment = new MovieDetailFragment();

            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_detail, fragment, MainActivity.DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent detailIntent =
                    new Intent(getBaseContext(), MovieDeatilActivity.class).setData(uri);
            startActivity(detailIntent);
        }
    }
}

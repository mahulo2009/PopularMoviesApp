package com.example.android.popularmovies.app;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


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
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creates the main fragment with the grid arrangement of movies posters
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view);
        setSupportActionBar(toolbar);

        MoviewViewPagerAdapter adapter = new MoviewViewPagerAdapter(getSupportFragmentManager(),getBaseContext());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

}

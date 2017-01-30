package com.example.android.popularmovies.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * The detail activity shows additional information, when the user tap on a movie.
 */
public class MovieDeatilActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_deatil);
        //Add the tool bar to action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view);
        setSupportActionBar(toolbar);
        //Display the back bottom.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Create the Movie Detail Fragment
        if (savedInstanceState == null) {
            //Inject the URI Movie
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.DETAIL_URI_KEY, getIntent().getData());
            //Create the Fragment with the arguments.
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            //Replace in the layout the element with the new Fragment.
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

}

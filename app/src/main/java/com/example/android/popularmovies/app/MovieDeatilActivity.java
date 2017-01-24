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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Startup the detail fragment.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_overview, new MovieDetailFragment())
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_trailer, new TrailerFragment())
                    .commit();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_review, new ReviewFragment())
                    .commit();
        }
    }

}

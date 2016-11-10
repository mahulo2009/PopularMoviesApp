package com.example.android.popularmovies.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

public class MovieDeatilActivity  extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_deatil);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MovieDetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_deatil,menu);
        return true;
    }

}

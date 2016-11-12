package com.example.android.popularmovies.app;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mhuertas on 10/11/16.
 */

public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();


    private List<Movie> mMovies;

    /**
     * The constructor. The context is used to inflate the layout file and the List contain the
     * movies to be displayed.
     *
     * @param context   The current context used to inflate the layout.
     * @param movies    A List of Movie objects to bed displayed.
     */
    public MovieAdapter(Activity context, List<Movie> movies)
    {
        super(context, 0,movies);
        mMovies=movies;
    }


    /**
     * Provides a view for an AdapterView (ListView, GridView, etc).
     *
     *
     * @param position      The Adapter view position that is requesting a view
     * @param convertView   The recycled view to populate
     * @param parent        The parent ViewGroup that is used for inflation.
     *
     * @return              The View for the position in the AdapterView
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        Log.v(LOG_TAG, "getView: " + position + " " + convertView);

        //Check if there is a View to be recycled rather than creating a new one.
        if (convertView==null) {
            LayoutInflater li = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = li.inflate(R.layout.list_item_movie,null);

            //ViewHolder pattern. The component views are stored inside the tag field of the Layout.
            //They can be access immediately without the need to look them up repeatedly.
            viewHolder = new ViewHolder();
            viewHolder.movie_poster = (ImageView)convertView.findViewById(R.id.movie_poster_imageview);
            viewHolder.position=position;

            convertView.setTag(viewHolder);
        } else {
            viewHolder =(ViewHolder)convertView.getTag();
        }

        final Movie movie = mMovies.get(position);
        if (movie != null) {
            Picasso.with(getContext()).
                    load(movie.getPoster_path()).
                    into(viewHolder.movie_poster);
        }

        return convertView;
    }

    /**
     * Class to hold the set of views.
     */
    public static class ViewHolder  {
        public ImageView movie_poster;
        public int position;
    }
}

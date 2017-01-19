package com.example.android.popularmovies.app;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import static com.example.android.popularmovies.app.R.layout.list_item_movie;

/**
 * Movie Cursor Adapter
 *
 * Created by mhuertas on 10/11/16.
 */

public class MovieAdapter extends CursorAdapter {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Activity context, Cursor c, int flags) {
        super(context, c,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(list_item_movie, parent, false);

        //Cache the GUI elements to improve performance.
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String poster_path = cursor.getString(MainFragment.COLUMN_MOVIE_POSTER_PATH);
        Picasso.with(context).
                load(poster_path).
                into(viewHolder.movie_poster);
    }

    /**
     * Class to hold the set of views.
     */
    public static class ViewHolder  {
        public ImageView movie_poster;

        public ViewHolder(View view) {
            movie_poster = (ImageView)view.findViewById(R.id.movie_poster_imageview);
        }
    }

}

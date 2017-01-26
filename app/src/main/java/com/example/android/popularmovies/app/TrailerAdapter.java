package com.example.android.popularmovies.app;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.example.android.popularmovies.app.R.layout.list_item_trailer;
import static com.example.android.popularmovies.app.TrailerFragment.COLUMN_TRAILER_KEY;
import static com.example.android.popularmovies.app.Utility.buildImageFirstFotogram;

/**
 * Created by mhuertas on 23/01/17.
 */

public class TrailerAdapter extends CursorAdapter {
    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(list_item_trailer, parent, false);
        //Cache the GUI elements to improve performance.
        TrailerAdapter.ViewHolder viewHolder = new TrailerAdapter.ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TrailerAdapter.ViewHolder viewHolder = (TrailerAdapter.ViewHolder) view.getTag();
        Picasso.with(context).
                load(buildImageFirstFotogram(cursor.getString(COLUMN_TRAILER_KEY))).
                into(viewHolder.iv_trailer);
        viewHolder.tv_movie_trailer_title.setText(cursor.getString(TrailerFragment.COLUMN_TRAILER_NAME));
    }

    public class ViewHolder {
        public TextView tv_movie_trailer_title;
        public ImageView iv_trailer;
        public ViewHolder(View view) {
            iv_trailer = (ImageView)view.findViewById(R.id.trailer_imageview);
            tv_movie_trailer_title = (TextView)view.findViewById(R.id.movie_trailer_title_textview);
        }
    }
}
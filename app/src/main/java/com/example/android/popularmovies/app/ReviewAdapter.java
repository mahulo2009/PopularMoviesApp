package com.example.android.popularmovies.app;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static com.example.android.popularmovies.app.R.layout.list_item_review;
import static com.example.android.popularmovies.app.ReviewFragment.COLUMN_REVIEW_AUTHOR;
import static com.example.android.popularmovies.app.ReviewFragment.COLUMN_REVIEW_CONTENT;
import static com.example.android.popularmovies.app.ReviewFragment.COLUMN_REVIEW_URL;

/**
 * Created by mhuertas on 23/01/17.
 */

public class ReviewAdapter  extends CursorAdapter {
    //TODO The number of words depending on layout
    private static int FIRST_N_WORDS = 5000;

    public ReviewAdapter(Activity context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(list_item_review, parent, false);
        //Cache the GUI elements to improve performance.
        ReviewAdapter.ViewHolder viewHolder = new ReviewAdapter.ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ReviewAdapter.ViewHolder viewHolder = (ReviewAdapter.ViewHolder) view.getTag();
        //Fill up the GUI
        viewHolder.tv_author.setText(cursor.getString(COLUMN_REVIEW_AUTHOR));
        viewHolder.tv_content.setText(Utility.getFirstNStrings(cursor.getString(COLUMN_REVIEW_CONTENT),FIRST_N_WORDS));
        viewHolder.tv_url.setText(Utility.buildUrlReadMore(cursor.getString(COLUMN_REVIEW_URL)));
    }

    public class ViewHolder {
        public TextView tv_author;
        public TextView tv_content;
        public TextView tv_url;
        public ViewHolder(View view) {
            tv_author = (TextView)view.findViewById(R.id.review_author_textview);
            tv_content = (TextView)view.findViewById(R.id.review_content_textview);
            tv_url = (TextView)view.findViewById(R.id.review_url_textview);
        }
    }
}
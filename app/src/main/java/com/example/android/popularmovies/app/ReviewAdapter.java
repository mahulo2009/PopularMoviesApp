package com.example.android.popularmovies.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.example.android.popularmovies.app.R.layout.list_item_review;
import static com.example.android.popularmovies.app.ReviewFragment.COLUMN_REVIEW_AUTHOR;
import static com.example.android.popularmovies.app.ReviewFragment.COLUMN_REVIEW_CONTENT;
import static com.example.android.popularmovies.app.ReviewFragment.COLUMN_REVIEW_URL;

/**
 * Created by mhuertas on 23/01/17.
 */

public class ReviewAdapter  extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterHolder>  {
    //TODO The number of words depending on layout
    private static int FIRST_N_WORDS = 50;

    private Cursor mCursor;
    private Context mContext;

    public ReviewAdapter(Context context) {
        mContext=context;
    }


    public class ReviewAdapterHolder extends RecyclerView.ViewHolder {

        public TextView tv_author;
        public TextView tv_content;
        public TextView tv_url;

        public ReviewAdapterHolder(View view) {
            super(view);
            tv_author = (TextView)view.findViewById(R.id.review_author_textview);
            tv_content = (TextView)view.findViewById(R.id.review_content_textview);
            tv_url = (TextView)view.findViewById(R.id.review_url_textview);

            //If the user tap a movie entry, a detail view is showed
            tv_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                }
            });
        }
    }

    @Override
    public ReviewAdapter.ReviewAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(list_item_review, parent, false);
        return new ReviewAdapter.ReviewAdapterHolder(view);

    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewAdapterHolder viewHolder, int position) {
        mCursor.moveToPosition(position);

        viewHolder.tv_author.setText(mCursor.getString(COLUMN_REVIEW_AUTHOR));
        viewHolder.tv_content.setText(Utility.getFirstNStrings(mCursor.getString(COLUMN_REVIEW_CONTENT),FIRST_N_WORDS));
        viewHolder.tv_url.setText(Utility.buildUrlReadMore(mCursor.getString(COLUMN_REVIEW_URL)));
    }

    @Override
    public int getItemCount() {
        if ( null != mCursor)
            return mCursor.getCount();
        return 0;
    }

    public void swapCursor(Cursor cursor) {
        if (cursor != null) {
            mCursor = cursor;
            notifyDataSetChanged();
        }
    }

    public Cursor getCursor() {
        return mCursor;
    }

}
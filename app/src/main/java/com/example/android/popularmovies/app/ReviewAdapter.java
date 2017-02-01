package com.example.android.popularmovies.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import static com.example.android.popularmovies.app.R.layout.list_item_review;
import static com.example.android.popularmovies.app.ReviewFragment.COLUMN_REVIEW_AUTHOR;
import static com.example.android.popularmovies.app.ReviewFragment.COLUMN_REVIEW_CONTENT;

/**
 * Created by mhuertas on 23/01/17.
 */

public class ReviewAdapter  extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterHolder>  {

    private static final String TAG = RecyclerView.class.getSimpleName();

    private static int MAX_NUMBER_WORDS = 1000;

    private Cursor mCursor;
    private Context mContext;

    public ReviewAdapter(Context context) {
        mContext=context;
    }

    public class ReviewAdapterHolder extends RecyclerView.ViewHolder {

        public TextView tv_author;
        public TextView tv_content;
        public ImageButton ib_readmore_up;
        public ImageButton ib_readmore_down;


        public ReviewAdapterHolder(View view) {
            super(view);
            tv_author = (TextView)view.findViewById(R.id.review_author_textview);
            tv_content = (TextView)view.findViewById(R.id.review_content_textview);
            ib_readmore_up = (ImageButton)view.findViewById(R.id.review_readmore_button_down);
            ib_readmore_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_content.setMaxLines(MAX_NUMBER_WORDS);
                    ib_readmore_up.setVisibility(View.INVISIBLE);
                    ib_readmore_down.setVisibility(View.VISIBLE);
                }
            });
            ib_readmore_down = (ImageButton)view.findViewById(R.id.review_readmore_button_up);
            ib_readmore_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_content.setMaxLines(mContext.getResources().getInteger(R.integer.trailer_review_max_lines));
                    ib_readmore_up.setVisibility(View.VISIBLE);
                    ib_readmore_down.setVisibility(View.INVISIBLE);
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
        viewHolder.tv_content.setText(mCursor.getString(COLUMN_REVIEW_CONTENT));
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
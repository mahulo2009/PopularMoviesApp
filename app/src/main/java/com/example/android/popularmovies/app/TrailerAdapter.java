package com.example.android.popularmovies.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import static com.example.android.popularmovies.app.R.layout.list_item_trailer;
import static com.example.android.popularmovies.app.Utility.buildImageFirstFotogram;

/**
 * Created by mhuertas on 23/01/17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public TrailerAdapter(Context context) {
        mContext=context;
    }

    public class TrailerViewHolder  extends RecyclerView.ViewHolder {

        public ImageView iv_trailer;

        public TrailerViewHolder(View view) {
            super(view);
            iv_trailer = (ImageView)view.findViewById(R.id.trailer_imageview);
            iv_trailer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (RecyclerView.NO_POSITION != position ) {
                        mCursor.moveToPosition(position);
                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                Utility.buildUriTrailer(mCursor.
                                        getString(MovieDetailFragment.COLUMN_TRAILER_KEY)));
                        webIntent.putExtra("force_fullscreen", true);
                        mContext.startActivity(webIntent);
                    }
                }
            });
        }
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(list_item_trailer, parent, false);
        return new TrailerAdapter.TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);

        Picasso.with(mContext).
                load(buildImageFirstFotogram(mCursor.getString(MovieDetailFragment.COLUMN_TRAILER_KEY))).
                into(viewHolder.iv_trailer);
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

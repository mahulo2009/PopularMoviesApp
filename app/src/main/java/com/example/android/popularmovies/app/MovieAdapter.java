package com.example.android.popularmovies.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.app.data.MovieContract;
import com.squareup.picasso.Picasso;

import static com.example.android.popularmovies.app.MainFragment.COLUMN_MOVIE_ID;
import static com.example.android.popularmovies.app.R.layout.list_item_movie;

/**
 * Movie Cursor Adapter
 *
 * Created by mhuertas on 10/11/16.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    private Cursor mCursor;
    private Context mContext;

    public MovieAdapter(Context context) {
        mContext=context;
    }

    /**
     * Class to hold the set of views.
     */
    public class MovieViewHolder  extends RecyclerView.ViewHolder {
        public ImageView movie_poster;


        public MovieViewHolder(View view) {
            super(view);
            movie_poster = (ImageView)view.findViewById(R.id.movie_poster_imageview);

            movie_poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (RecyclerView.NO_POSITION != position ) {
                        mCursor.moveToPosition(position);

                        Uri uri = MovieContract.MovieEntry.buildMovieUriAPIId(mCursor.getString(COLUMN_MOVIE_ID));
                        Intent detailIntent =
                                new Intent(mContext, MovieDeatilActivity.class).setData(uri);
                        mContext.startActivity(detailIntent);
                    }
                }
            });
        }
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(list_item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String poster_path = mCursor.getString(MainFragment.COLUMN_MOVIE_POSTER_PATH);
        Picasso.with(mContext).
                load(poster_path).
                into(holder.movie_poster);
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

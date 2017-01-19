package com.example.android.popularmovies.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.popularmovies.app.data.Movie;

/**
 * Created by mhuertas on 14/01/17.
 */
public class MovieViewPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    private static final int POPULAR = 0;
    private static final int RATED = 1;
    private static final int FAVOURITE = 2;

    public MovieViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext=context;
    }

    @Override
    public Fragment getItem(int position) {
        MainFragment f = new MainFragment();
        Bundle bundle = new Bundle();

        switch (position) {
            case POPULAR:
                bundle.putString(MainFragment.ORDER_BY_KEY, Movie.POPULAR_MOVIE);
                break;
            case RATED:
                bundle.putString(MainFragment.ORDER_BY_KEY,Movie.TOP_RATED_MOVIE);
                break;
            case FAVOURITE:
                bundle.putString(MainFragment.ORDER_BY_KEY,Movie.FAVOURITE_MOVIE);
                break;
            default:
                break;
        }
        f.setArguments(bundle);
        return f;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case POPULAR:
                return mContext.getResources().getString(R.string.title_tab_popular);
            case RATED:
                return mContext.getResources().getString(R.string.title_tab_rated);
            case FAVOURITE:
                return mContext.getResources().getString(R.string.title_tab_favourite);
        }

        return "";
    }
}

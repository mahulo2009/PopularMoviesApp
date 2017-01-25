package com.example.android.popularmovies.app;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by mhuertas on 24/01/17.
 */

public class MovieDetailPagerAdapter  extends FragmentPagerAdapter {

    private Context mContext;

    private static final int OVERVIEW = 0;
    private static final int TRAILER = 1;
    private static final int REVIEW = 2;

    public MovieDetailPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext=context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case OVERVIEW:
                    return new MovieDetailFragment();
            case TRAILER:
                return new TrailerFragment();
            case REVIEW:
                return new ReviewFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        //TODO If the movie has not got trailer or reviews, change this number.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case OVERVIEW:
                return mContext.getResources().getString(R.string.title_tab_overview);
            case TRAILER:
                return mContext.getResources().getString(R.string.title_tab_trailer);
            case REVIEW:
                return mContext.getResources().getString(R.string.title_tab_review);
        }
        return "";
    }

}

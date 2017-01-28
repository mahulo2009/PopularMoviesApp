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
    private static final int REVIEW = 1;

    public MovieDetailPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext=context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case OVERVIEW:
                    return new MovieDetailFragment();
            case REVIEW:
                return new ReviewFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        //TODO If the movie has not got trailer or reviews, change this number.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case OVERVIEW:
                return mContext.getResources().getString(R.string.title_tab_overview);
            case REVIEW:
                return mContext.getResources().getString(R.string.title_tab_review);
        }
        return "";
    }

}

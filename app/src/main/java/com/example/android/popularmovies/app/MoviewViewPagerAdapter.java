package com.example.android.popularmovies.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by mhuertas on 14/01/17.
 */
public class MoviewViewPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    private static final int POPULAR = 0;
    private static final int RATED = 1;
    private static final int FAVOURITE = 2;

    public MoviewViewPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        mContext=context;
    }

    @Override
    public Fragment getItem(int position) {
        MainFragment f = new MainFragment();
        Bundle bundle = new Bundle();

        switch (position) {
            case POPULAR:
                bundle.putString(MainFragment.ORDER_BY_KEY,"popular");
                break;
            case RATED:
                bundle.putString(MainFragment.ORDER_BY_KEY,"top_rated");
                break;
            case FAVOURITE:
                //TODO TO INCLUDE THE FAVOURITE FRAGMENT
                bundle.putString(MainFragment.ORDER_BY_KEY,"top_rated");
                break;
            default:
                bundle.putString(MainFragment.ORDER_BY_KEY,"top_rated");
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

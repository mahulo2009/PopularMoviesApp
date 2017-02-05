package com.example.android.popularmovies.app.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by mhuertas on 4/02/17.
 */
public class PopulatMovieSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static PopularMovieSyncAdapter sPopularMovieSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SunshineSyncService", "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock) {
            if (sPopularMovieSyncAdapter == null) {
                sPopularMovieSyncAdapter = new PopularMovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sPopularMovieSyncAdapter.getSyncAdapterBinder();
    }

}

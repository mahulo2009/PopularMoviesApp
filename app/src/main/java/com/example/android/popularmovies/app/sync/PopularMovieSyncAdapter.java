package com.example.android.popularmovies.app.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.android.popularmovies.app.BuildConfig;
import com.example.android.popularmovies.app.R;
import com.example.android.popularmovies.app.Utility;
import com.example.android.popularmovies.app.data.Movie;
import com.example.android.popularmovies.app.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import static com.example.android.popularmovies.app.Utility.getImagePath;

/**
 * Created by mhuertas on 4/02/17.
 */
public class PopularMovieSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String TAG = PopularMovieSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in milliseconds.
    // 60 seconds (1 minute)  180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public PopularMovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        PopularMovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }


    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);

        }
        return newAccount;
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }



    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              SyncResult syncResult) {
        Log.d(TAG, "onPerformSync: ");

        String params[] = {Movie.POPULAR_MOVIE,Movie.TOP_RATED_MOVIE};

        for (String param: params) {

            Uri builtUri = buildUri(param);
            Log.v(TAG, "Movies URL: " + builtUri.toString());

            try {
                //Build the URI for the API Movie service
                URL url = new URL(builtUri.toString());
                //Parse the responce from json to java pojo.
                Movie[] movies = parseResponce(Utility.request(url));
                //Insert into the database.
                insertDB(movies, param);
            } catch (MalformedURLException e) {
                Log.e(TAG, e.getMessage());
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }

    }

    /**
     * Build the URI to access the API movie service
     *
     * @param orderBy   Order by popular or top rated.
     * @return
     */
    private Uri buildUri(String orderBy) {
        // Construct the URL
        final String APPID_PARAM = "api_key";

        Uri.Builder builder = new Uri.Builder();
        Uri builtUri = builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(orderBy)
                .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();
        return builtUri;
    }

    /**
     * Build a list of Movie objects from the JSON string from the API request.
     *
     * @param movieJsonStr      JSON string from the API request.
     *
     * @return                  List of movie objects
     *
     * @throws JSONException    Exception JSON parsing
     */
    private Movie[] parseResponce(String movieJsonStr)
            throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String MV_ID = "id";
        final String MV_TITLE = "original_title";
        final String MV_POSTER_PATH = "poster_path";
        final String MV_BACKDROP_PATH = "backdrop_path";
        final String MV_OVERVIEW = "overview";
        final String MV_VOTE_AVERAGE = "vote_average";
        final String MV_RELEASE_DATE = "release_date";
        final String MV_LIST = "results";

        //Parse the JSON result and get the movie list
        JSONObject moviesJson = new JSONObject(movieJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(MV_LIST);

        //Store the result into an array.
        Movie[] results = new Movie[moviesArray.length()];
        for(int i = 0; i < moviesArray.length(); i++) {
            // Get the JSON object representing a movie
            JSONObject movieJson = moviesArray.getJSONObject(i);

            //Build the movie entry string
            Movie movie = new Movie();
            movie.setId(movieJson.getString(MV_ID));
            movie.setTitle(movieJson.getString(MV_TITLE));
            movie.setPoster_path(getImagePath(movieJson.getString(MV_POSTER_PATH),"w185"));
            movie.setBackdrop_path(Utility.getImagePath(movieJson.getString(MV_BACKDROP_PATH),"w342"));
            movie.setOverview(movieJson.getString(MV_OVERVIEW));
            movie.setVote_average(movieJson.getString(MV_VOTE_AVERAGE));
            movie.setRelease_date(movieJson.getString(MV_RELEASE_DATE));

            results[i] = movie;
        }

        for (Movie s : results) {
            Log.v(TAG, "Movie entry: " + s);
        }
        return results;
    }

    /**
     * Insert into the database, table movie, the result.
     *
     * @param movies    The list of movies
     * @param orderBy   The order criteria.
     *
     */
    private void insertDB(Movie[] movies,String orderBy) {
        ContentValues[] values = new ContentValues[movies.length];
        int i=0;
        for (Movie movie : movies) {
            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, movie.getPoster_path());
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH, movie.getBackdrop_path());
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getRelease_date());
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE, movie.getVote_average());
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_CRITERIA,orderBy );
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());

            values[i++]=movieValues;
        }
        getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI,values);
    }

}

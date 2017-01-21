package com.example.android.popularmovies.app;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.popularmovies.app.data.MovieContract;
import com.example.android.popularmovies.app.data.MovieDbHelper;
import com.example.android.popularmovies.app.data.MovieProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.example.android.popularmovies.app.TestUtilities.BULK_INSERT_RECORDS_TO_INSERT;
import static com.example.android.popularmovies.app.data.MovieContract.MovieEntry.buildMovieUri;
import static com.example.android.popularmovies.app.data.MovieContract.MovieEntry.buildMovieUriAPIId;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by mhuertas on 16/01/17.
 */

@RunWith(AndroidJUnit4.class)
public class TestProvider {


    public void deleteAllRecordsFromProvider() {
        Context mContext = InstrumentationRegistry.getTargetContext();

        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                MovieContract.MovieFavoriteEntry.CONTENT_URI,
                null,
                null
        );


        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Review table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Trailer table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieContract.MovieFavoriteEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from MovieFavorite table during delete", 0, cursor.getCount());
        cursor.close();
    }

    @Before
    public void setUp() throws Exception {
        deleteAllRecordsFromProvider();
    }

    @After
    public void shutDown() throws Exception {
        deleteAllRecordsFromProvider();
    }


    @Test
    public void testProviderRegistry() {
        Context mContext = InstrumentationRegistry.getTargetContext();

        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    @Test
    public void testGetType() {
        Context mContext = InstrumentationRegistry.getTargetContext();

        // content://com.example.android.popularmovies.app/movie/
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.popularmovies.app/movie
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

        String testMovieID = "1";
        // content://com.example.android.popularmovies.app/movie/1
        type = mContext.getContentResolver().getType(
                buildMovieUriAPIId(testMovieID));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the MovieEntry CONTENT_URI with id should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);

        //TODO ADD Review and Trail

        // content://com.example.android.popularmovies.app/movie_favorite/
        type = mContext.getContentResolver().getType(MovieContract.MovieFavoriteEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.popularmovies.app/movie_favorite
        assertEquals("Error: the MovieFavoriteEntry CONTENT_URI should return MovieFavoriteEntry.CONTENT_TYPE",
                MovieContract.MovieFavoriteEntry.CONTENT_TYPE, type);

        testMovieID = "1";
        // content://com.example.android.popularmovies.app/movie_favorite/1
        type = mContext.getContentResolver().getType(
                MovieContract.MovieFavoriteEntry.buildMovieFavoriteUri(1));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals("Error: the MovieFavoriteEntry CONTENT_URI with id should return MovieFavoriteEntry.CONTENT_TYPE",
                MovieContract.MovieFavoriteEntry.CONTENT_ITEM_TYPE, type);

    }

    @Test
    public void testBasicMovieQuery() {
        Context mContext = InstrumentationRegistry.getTargetContext();
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        ContentValues movieValues = TestUtilities.createMovieValues();
        db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieValues);

        // Test the basic content provider query
        Cursor c = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testBasicMovieQuery", c, movieValues);
    }

    @Test
    public void testBasicMovieFavoriteQuery() {
        Context mContext = InstrumentationRegistry.getTargetContext();
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        ContentValues movieFavoriteValues = TestUtilities.createMovieFavoriteValues(1);
        db.insert(MovieContract.MovieFavoriteEntry.TABLE_NAME, null, movieFavoriteValues);

        // Test the basic content provider query
        Cursor c = mContext.getContentResolver().query(
                MovieContract.MovieFavoriteEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testBasicMovieFavoriteQuery", c, movieFavoriteValues);


        c = mContext.getContentResolver().query(
                MovieContract.MovieFavoriteEntry.buildMovieFavoriteUri(1),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testBasicMovieQuery", c, movieFavoriteValues);
    }


        @Test
    public void testBasicReviewQuery() {
        Context mContext = InstrumentationRegistry.getTargetContext();
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        ContentValues reviewValues = TestUtilities.createReviewValues(1);
        db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, reviewValues);

        // Test the basic content provider query
        Cursor c = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testBasicReviewQuery", c, reviewValues);
    }

    @Test
    public void testBasicTrailerQuery() {
        Context mContext = InstrumentationRegistry.getTargetContext();
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        ContentValues trailerValues = TestUtilities.createTrailerValues(1);
        db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, trailerValues);

        // Test the basic content provider query
        Cursor c = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testBasicTrailerQuery", c, trailerValues);
    }

    @Test
    public void testMovieDetailQuery() {
        Context mContext = InstrumentationRegistry.getTargetContext();
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        ContentValues movieValues = TestUtilities.createMovieValues();
        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieValues);


        ContentValues reviewValues = TestUtilities.createReviewValues(movieRowId);
        db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, reviewValues);

        ContentValues trailerValues = TestUtilities.createTrailerValues(movieRowId);
        db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, trailerValues);

        // Test the basic content provider query
        Cursor c = mContext.getContentResolver().query(
                buildMovieUri(movieRowId),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testBasicMovieQuery", c, movieValues);
    }

    @Test
    public void testMovieFavoriteTrueQuery() {
        Context mContext = InstrumentationRegistry.getTargetContext();
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        ContentValues movieValues = TestUtilities.createMovieValues();
        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieValues);

        ContentValues movieFavoriteValues = TestUtilities.createMovieFavoriteValues(movieRowId);
        db.insert(MovieContract.MovieFavoriteEntry.TABLE_NAME, null, movieFavoriteValues);

        final String[] DETAIL_COLUMNS = {
                MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
                MovieContract.MovieFavoriteEntry.COLUMN_FAVORITE_MOVIE_ID

        };
        final int COLUMN_FAVORITE_ID = 1;
        // Test the basic content provider query
        Cursor c = mContext.getContentResolver().query(
                buildMovieUri(movieRowId),
                DETAIL_COLUMNS,
                null,
                null,
                null
        );
        assertTrue(c.moveToFirst());
        String _id = c.getString(COLUMN_FAVORITE_ID);
        assertEquals("1",_id);
    }

    @Test
    public void testMovieFavoriteFalseQuery() {
        Context mContext = InstrumentationRegistry.getTargetContext();
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        ContentValues movieValues = TestUtilities.createMovieValues();
        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieValues);

        final String[] DETAIL_COLUMNS = {
                MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
                MovieContract.MovieFavoriteEntry.COLUMN_FAVORITE_MOVIE_ID

        };
        final int COLUMN_FAVORITE_ID = 1;
        // Test the basic content provider query
        Cursor c = mContext.getContentResolver().query(
                buildMovieUri(movieRowId),
                DETAIL_COLUMNS,
                null,
                null,
                null
        );
        assertTrue(c.moveToFirst());
        String _id = c.getString(COLUMN_FAVORITE_ID);
        assertEquals(null, _id);
    }

    @Test
    public void testMovieTrailerQuery() {
        Context mContext = InstrumentationRegistry.getTargetContext();
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        ContentValues movieValues = TestUtilities.createMovieValues();
        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieValues);


        ContentValues trailerValues = TestUtilities.createTrailerValues(movieRowId);
        db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, trailerValues);

        // Test the basic content provider query
        Cursor c = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.buildTrailerUri(movieRowId),
                null,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testBasicMovieQuery", c, trailerValues);
    }

    @Test
    public void testMovieFavoriteQuery() {
        //TODO.
    }

    @Test
    public void testBulkInsert() {
        Context mContext = InstrumentationRegistry.getTargetContext();

        ContentValues[] bulkInsertContentValues = TestUtilities.createBulkInsertMovieValues();
        int insertCount = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, bulkInsertContentValues);
        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null
        );
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating MovieEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

}

package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.popularmovies.app.data.MovieContract;
import com.example.android.popularmovies.app.data.MovieDbHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestDb {

    void deleteTheDatabase() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        appContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    @Before
    public void setUp() throws Exception {
        deleteTheDatabase();
    }

    @After
    public void shutDown() throws Exception {
        deleteTheDatabase();
    }


    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.android.popularmovies.app", appContext.getPackageName());
    }

    @Test
    public void testCreateDb() throws Throwable {

        //Database open correctly.
        SQLiteDatabase db = new MovieDbHelper(
                InstrumentationRegistry.getTargetContext()).getWritableDatabase();
        assertEquals(true, db.isOpen());

        //The table exists
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.ReviewEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.TrailerEntry.TABLE_NAME);

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        c.close();
        db.close();
    }

    @Test
    public void testReviewTable() {

        long movideRowId = insertMovie();
        assertFalse("Error: Review Not Inserted Correctly", movideRowId == -1L);

        SQLiteDatabase db = new MovieDbHelper(
                InstrumentationRegistry.getTargetContext()).getWritableDatabase();


        ContentValues reviewValues = TestUtilities.createReviewValues(movideRowId);

        long reviewRowId = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, reviewValues);
        assertTrue(reviewRowId != -1);

        Cursor reviewCursor = db.query(
                MovieContract.ReviewEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );
        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue("Error: No Records returned from location query", reviewCursor.moveToFirst());

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord("testInsertReadDb reviewEntry failed to validate",
                reviewCursor, reviewValues);

        reviewCursor.close();
        db.close();
    }

    @Test
    public void testTrailerTable() {

        long movideRowId = insertMovie();
        assertFalse("Error: Trailer Not Inserted Correctly", movideRowId == -1L);

        SQLiteDatabase db = new MovieDbHelper(
                InstrumentationRegistry.getTargetContext()).getWritableDatabase();


        ContentValues trailerValues = TestUtilities.createTrailerValues(movideRowId);

        long trailerRowId = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, trailerValues);
        assertTrue(trailerRowId != -1);

        Cursor trailerCursor = db.query(
                MovieContract.TrailerEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );
        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue("Error: No Records returned from location query", trailerCursor.moveToFirst());

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord("testInsertReadDb reviewEntry failed to validate",
                trailerCursor, trailerValues);

        db.close();
    }

    public long insertMovie() {
        SQLiteDatabase db = new MovieDbHelper(
                InstrumentationRegistry.getTargetContext()).getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieValues();

        long movieRowId;
        movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        db.close();

        return movieRowId;
    }

    @Test
    public void testMovieFavouriteTable() {
        SQLiteDatabase db = new MovieDbHelper(
                InstrumentationRegistry.getTargetContext()).getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieFavoriteValues(1);

        long movieFavoriteRowId;
        movieFavoriteRowId = db.insert(MovieContract.MovieFavoriteEntry.TABLE_NAME, null, testValues);

        Cursor favoriteCursor = db.query(
                MovieContract.MovieFavoriteEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );
        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue("Error: No Records returned from location query", favoriteCursor.moveToFirst());

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord("testInsertReadDb movieFavorite failed to validate",
                favoriteCursor, testValues);

        favoriteCursor.close();
        db.close();
    }
}


package com.example.android.popularmovies.app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by mhuertas on 16/01/17.
 */
public class MovieProvider extends ContentProvider {

    private MovieDbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int TRAILER = 200;
    static final int TRAILER_MOVIE_ID = 201;
    static final int REVIEW = 300;
    static final int REVIEW_MOVIE_ID = 301;

    static final int MOVIE_FAVORITE = 400;
    static final int MOVIE_FAVORITE_WITH_ID = 401;

    private static final SQLiteQueryBuilder sMovieWithFavoriteQueryBuilder;
    static{
        sMovieWithFavoriteQueryBuilder = new SQLiteQueryBuilder();
        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sMovieWithFavoriteQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " LEFT JOIN " +
                        MovieContract.MovieFavoriteEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.MovieFavoriteEntry.TABLE_NAME +
                        "." + MovieContract.MovieFavoriteEntry.COLUMN_FAVORITE_MOVIE_ID);
    }

    //movie.id = ?
    private static final String sMovieSelection =
            MovieContract.MovieEntry.TABLE_NAME+
                    "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";
    private Cursor getMovieByID(Uri uri, String[] projection, String sortOrder) {
        String movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        return sMovieWithFavoriteQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieSelection,
                new String[]{movieId},
                null,
                null,
                sortOrder
        );
    }

    //movie_favorite.id = ?
    private static final String sMovieFavoriteSelection =
            MovieContract.MovieFavoriteEntry.TABLE_NAME+
                    "." + MovieContract.MovieFavoriteEntry.COLUMN_FAVORITE_MOVIE_ID + " = ? ";
    private Cursor getMovieFavoriteByID(Uri uri, String[] projection, String sortOrder) {
        String movieFavoriteId = MovieContract.MovieFavoriteEntry.getMovieFavoriteIdFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.MovieFavoriteEntry.TABLE_NAME,
                projection,
                sMovieFavoriteSelection,
                new String[]{movieFavoriteId},
                null,
                null,
                sortOrder
        );
    }

    //trailer.movie_id = ?
    private static final String sTralierByMovieSelection =
            MovieContract.TrailerEntry.TABLE_NAME+
                    "." + MovieContract.TrailerEntry.COLUMN_TRAILER_MOVIE_ID + " = ? ";
    private Cursor getTrailerByMovieID(Uri uri, String[] projection, String sortOrder) {
        String movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.TrailerEntry.TABLE_NAME,
                projection,
                sTralierByMovieSelection,
                new String[]{movieId},
                null,
                null,
                sortOrder
        );
    }

    //review.movie_id = ?
    private static final String sRevieByMovieSelection =
            MovieContract.ReviewEntry.TABLE_NAME+
                    "." + MovieContract.ReviewEntry.COLUMN_REVIEW_MOVIE_ID + " = ? ";
    private Cursor getReviewByMovieID(Uri uri, String[] projection, String sortOrder) {
        String movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.ReviewEntry.TABLE_NAME,
                projection,
                sRevieByMovieSelection,
                new String[]{movieId},
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE+"/#", MOVIE_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER);
        matcher.addURI(authority, MovieContract.PATH_TRAILER+"/#", TRAILER_MOVIE_ID);
        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);
        matcher.addURI(authority, MovieContract.PATH_REVIEW+"/#", REVIEW_MOVIE_ID);
        matcher.addURI(authority, MovieContract.PATH_MOVIE_FAVORITE, MOVIE_FAVORITE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE_FAVORITE+"/#", MOVIE_FAVORITE_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                retCursor = sMovieWithFavoriteQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIE_WITH_ID:
                retCursor = getMovieByID(uri,projection,sortOrder);
                break;
            case TRAILER:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TRAILER_MOVIE_ID:
                retCursor = getTrailerByMovieID(uri,projection,sortOrder);
                break;

            case REVIEW:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case REVIEW_MOVIE_ID:
                retCursor = getReviewByMovieID(uri,projection,sortOrder);
                break;
            case MOVIE_FAVORITE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieFavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIE_FAVORITE_WITH_ID:
                retCursor = getMovieFavoriteByID(uri,projection,sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Nullable
    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);
        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case TRAILER_MOVIE_ID:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case REVIEW_MOVIE_ID:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case MOVIE_FAVORITE:
                return MovieContract.MovieFavoriteEntry.CONTENT_TYPE;
            case MOVIE_FAVORITE_WITH_ID:
                return MovieContract.MovieFavoriteEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILER: {
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEW: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOVIE_FAVORITE: {
                long _id = db.insert(MovieContract.MovieFavoriteEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieFavoriteEntry.buildMovieFavoriteUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIE: {
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case TRAILER: {
                rowsDeleted = db.delete(MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case REVIEW: {
                rowsDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case MOVIE_FAVORITE: {
                rowsDeleted = db.delete(MovieContract.MovieFavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //TODO
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case TRAILER: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case REVIEW: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case MOVIE_FAVORITE: {
                //TODO
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }

}

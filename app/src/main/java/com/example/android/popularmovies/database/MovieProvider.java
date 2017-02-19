package com.example.android.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by magicleon on 18/02/17.
 */

public class MovieProvider extends ContentProvider {
    public static final int CODE_MOVIE = 101;
    private static UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE,CODE_MOVIE);


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
        Cursor cursor;
        switch (sUriMatcher.match(uri)){
            case CODE_MOVIE:{
                SQLiteDatabase db = mOpenHelper.getReadableDatabase();
                cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                                    projection,selection,selectionArgs,
                                    null,
                                    null,
                                    sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri returnUri = null;

        switch (sUriMatcher.match(uri)){
            case CODE_MOVIE:{
                SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,values);
                if (id>0){
                    returnUri =  MovieContract.MovieEntry.buildMovieUri(id);
                }
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (sUriMatcher.match(uri)){
            case CODE_MOVIE:{
                SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                return db.delete(MovieContract.MovieEntry.TABLE_NAME,selection,selectionArgs);
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

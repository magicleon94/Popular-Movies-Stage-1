package com.example.android.popularmovies.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by magicleon on 18/02/17.
 */

public class MovieContract {
    public static String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static String PATH_MOVIE = "movies";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String TABLE_NAME = "BookmarkedMovies";
        public static final String MOVIE_ID = "id";
        public static final String MOVIE_TITLE = "title";
        public static final String MOVIE_OVERVIEW = "overview";
        public static final String MOVIE_POSTER= "poster";
        public static final String MOVIE_AVG = "voteAverage";
        public static final String MOVIE_VOTES = "voteCount";
        public static final String MOVIE_RELEASE_DATE = "releaseDate";
        public static final String MOVIE_TRAILERS = "trailers";
        public static final String MOVIE_REVIEWS = "reviews";

        public static final int IDX_ID = 0;
        public static final int IDX_TITLE = 1;
        public static final int IDX_OVERVIEW = 2;
        public static final int IDX_POSTER_PATH = 3;
        public static final int IDX_VOTE_COUNT = 5;
        public static final int IDX_VOTE_AVERAGE = 4;
        public static final int IDX_RELEASE_DATE = 6;
        public static final int IDX_TRAILERS = 6;
        public static final int IDX_REVIEWS = 7;

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(BASE_CONTENT_URI,id);
        }

    }
}

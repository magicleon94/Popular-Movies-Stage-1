package com.example.android.popularmovies;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static com.example.android.popularmovies.BuildConfig.MY_API_KEY;

class NetworkUtils {

    private final String LOG_TAG = NetworkUtils.class.getSimpleName();


    URL buildMoviesUrl(int page,String sorting) {
        String API_BASE_URL = "http://api.themoviedb.org/3/movie/";
        String API_PARAM_PAGE = "page";
        String API_PARAM_KEY = "api_key";
        String API_LANGUAGE = "language";
        String API_POSTER_LANGUAGE = "include_image_language";
        Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath(sorting)
                .appendQueryParameter(API_PARAM_PAGE, String.valueOf(page))
                .appendQueryParameter(API_PARAM_KEY, MY_API_KEY)
                .appendQueryParameter(API_LANGUAGE, "en")
                .appendQueryParameter(API_POSTER_LANGUAGE, "en")
                .build();

        Log.d(LOG_TAG, "Query URI: " + builtUri.toString());

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    URL buildTrailersUrl(long id){
        String API_BASE_URL = "http://api.themoviedb.org/3/movie/";
        String API_PARAM_KEY = "api_key";
        String API_TRAILERS_PATH = "videos";

        Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath(Long.toString(id))
                .appendPath(API_TRAILERS_PATH)
                .appendQueryParameter(API_PARAM_KEY, MY_API_KEY)
                .build();

        Log.d(LOG_TAG, "Query URI: " + builtUri.toString());

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
    URL buildReviewsUrl(long id){
        String API_BASE_URL = "http://api.themoviedb.org/3/movie/";
        String API_PARAM_KEY = "api_key";
        String API_REVIEWS_PATH = "reviews";

        Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath(Long.toString(id))
                .appendPath(API_REVIEWS_PATH)
                .appendQueryParameter(API_PARAM_KEY, MY_API_KEY)
                .build();

        Log.d(LOG_TAG, "Query URI: " + builtUri.toString());

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
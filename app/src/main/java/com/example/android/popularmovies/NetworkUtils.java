package com.example.android.popularmovies;

/**
 * Created by magicleon on 28/01/17.
 */

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

class NetworkUtils {

    private final String LOG_TAG = NetworkUtils.class.getSimpleName();

    private Context applicationContext;

    NetworkUtils(Context context){
        applicationContext = context;
    }
    URL buildUrl(int page) {
        String API_SORTING = applicationContext.getSharedPreferences(applicationContext.getString(R.string.movie_preferences), Context.MODE_PRIVATE).getString("sorting", "popular");
        String API_BASE_URL = "http://api.themoviedb.org/3/movie/";
        String API_PARAM_PAGE = "page";
        String API_PARAM_KEY = "api_key";
        String API_LANGUAGE = "language";
        String API_POSTER_LANGUAGE = "include_image_language";

        Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendPath(API_SORTING)
                .appendQueryParameter(API_PARAM_PAGE, String.valueOf(page))
                .appendQueryParameter(API_PARAM_KEY, applicationContext.getString(R.string.api_key))
                .appendQueryParameter(API_LANGUAGE,"en")
                .appendQueryParameter(API_POSTER_LANGUAGE,"en")
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
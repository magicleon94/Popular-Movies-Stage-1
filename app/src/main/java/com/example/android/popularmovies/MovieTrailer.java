package com.example.android.popularmovies;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by magicleon on 18/02/17.
 */

public class MovieTrailer {

    public static final String ID_KEY = "id";
    public static final String LANGUAGE_KEY = "iso_639_1";
    public static final String TRAILER_KEY = "key";
    public static final String NAME_KEY = "name";
    public static final String SITE_KEY = "site";

    public final String id;
    public final String language;
    public final String key;
    public final String name;
    public final String site;

    public MovieTrailer(String id,String language, String key, String name, String site) {
        this.id = id;
        this.language = language;
        this.key = key;
        this.name = name;
        this.site = site;
    }

    public static MovieTrailer getMovieDetailsFromJson(JSONObject jsonObject) throws JSONException {
        return new MovieTrailer(jsonObject.getString(ID_KEY),
                jsonObject.getString(LANGUAGE_KEY),
                jsonObject.getString(TRAILER_KEY),
                jsonObject.getString(NAME_KEY),
                jsonObject.getString(SITE_KEY));
    }

    public static ArrayList<Uri> getTrailer(){
        return null;
    }
}

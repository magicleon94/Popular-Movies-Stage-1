/**
 * Created by magicleon on 28/01/17.
 */
package com.example.android.popularmovies;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Movie implements Parcelable

{
    public static final String EXTRA_MOVIE = "com.example.magicleon.movierowser.EXTRA_MOVIE";
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE ="title";
    public static final String KEY_OVERVIEW = "overview";
    public static final String KEY_POSTER_PATH ="poster_path";
    public static final String KEY_VOTE_COUNT = "vote_count";
    public static final String KEY_VOTE_AVERAGE = "vote_average";
    public static final String KEY_RELEASE_DATE = "release_date";

    public final long id;
    public final String title;
    public final String overview;
    public final String poster_path;
    public final double vote_average;
    public final long vote_count;
    public final String release_date;
    public ArrayList<Trailer> trailers;
    public ArrayList<Review> reviews;

    public Movie(long id, String title, String overview, String poster_path, double vote_average, long vote_count, String release_date)
    {
        this.id=id;
        this.title=title;
        this.overview=overview;
        this.poster_path=poster_path;
        this.vote_average=vote_average;
        this.vote_count=vote_count;
        this.release_date=release_date;
        this.trailers = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }


    public Movie(ArrayList<Review> reviews, ArrayList<Trailer> trailers, String release_date, long vote_count, double vote_average, String poster_path, String overview, String title, long id) {
        this.reviews = reviews;
        this.trailers = trailers;
        this.release_date = release_date;
        this.vote_count = vote_count;
        this.vote_average = vote_average;
        this.poster_path = poster_path;
        this.overview = overview;
        this.title = title;
        this.id = id;
    }

    public Movie(Bundle bundle)
    {
        this(bundle.getLong(KEY_ID),
                bundle.getString(KEY_TITLE),
                bundle.getString(KEY_OVERVIEW),
                bundle.getString(KEY_POSTER_PATH),
                bundle.getDouble(KEY_VOTE_AVERAGE),
                bundle.getLong(KEY_VOTE_COUNT),
                bundle.getString(KEY_RELEASE_DATE));
    }


    protected Movie(Parcel in) {
        id = in.readLong();
        title = in.readString();
        overview = in.readString();
        poster_path = in.readString();
        vote_average = in.readDouble();
        vote_count = in.readLong();
        release_date = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        bundle.putLong(KEY_ID, id);
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_OVERVIEW, overview);
        bundle.putString(KEY_POSTER_PATH, poster_path);
        bundle.putDouble(KEY_VOTE_AVERAGE, vote_average);
        bundle.putLong(KEY_VOTE_COUNT, vote_count);
        bundle.putString(KEY_RELEASE_DATE,release_date);

        return bundle;
    }

    public static Movie getMovieFromJson(JSONObject jsonObject) throws JSONException
    {
        return new Movie(jsonObject.getLong(KEY_ID),
                jsonObject.getString(KEY_TITLE),
                jsonObject.getString(KEY_OVERVIEW),
                jsonObject.getString(KEY_POSTER_PATH),
                jsonObject.getDouble(KEY_VOTE_AVERAGE),
                jsonObject.getLong(KEY_VOTE_COUNT),
                jsonObject.getString(KEY_RELEASE_DATE));
    }

    //returns the uri needed for Picasso.

    public Uri getPosterUri(String size)
    {
        final String BASE_URL = "http://image.tmdb.org/t/p";

        return Uri.parse(BASE_URL).buildUpon().appendPath(size).appendEncodedPath(poster_path).build();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(poster_path);
        dest.writeDouble(vote_average);
        dest.writeLong(vote_count);
        dest.writeString(release_date);
    }
}

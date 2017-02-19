package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {
    private final String TAG = this.getClass().getSimpleName();
    Movie mMovie;
    TextView mTitle;
    TextView mReleaseDate;
    TextView mAverage;
    TextView mPlot;
    ImageView mPoster;
    ArrayList<Trailer> mTrailers;
    ArrayList<Review> mReviews;

    private static final int LOADER_ID = 818;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitle = (TextView) findViewById(R.id.tv_details_title);
        mReleaseDate = (TextView) findViewById(R.id.tv_details_release_date);
        mAverage = (TextView) findViewById(R.id.tv_details_average);
        mPlot = (TextView) findViewById(R.id.tv_details_plot);
        mPoster = (ImageView) findViewById(R.id.detail_poster);

        Intent callerIntent = getIntent();
        if (callerIntent.hasExtra(Movie.EXTRA_MOVIE)){
            mMovie = new Movie(callerIntent.getBundleExtra(Movie.EXTRA_MOVIE));
            mTitle.setText(mMovie.title);
            mReleaseDate.setText(String.format(getString(R.string.release_date), mMovie.release_date));
            mAverage.setText(String.format(getString(R.string.vote_average), mMovie.vote_average));
            mPlot.setText(mMovie.overview);
            Picasso.with(this).load(mMovie.getPosterUri(getString(R.string.poster_default_size))).into(mPoster);
            Log.d(TAG,"Preparing loader");
            getSupportLoaderManager().restartLoader(LOADER_ID,null,this);
            Log.d(TAG,"Started");
        }

    }

    @Override
    public Loader<Object> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<Object>(this) {
            @Override
            protected void onStartLoading() {
                Log.d(TAG,"Start load");
                forceLoad();
            }

            @Override
            public Void loadInBackground() {
                Log.d(TAG,"Load in background");
                long id = mMovie.id;
                NetworkUtils networker = new NetworkUtils(getApplicationContext());
                URL requestTrailersUrl = networker.buildTrailersUrl(id);
                URL requestReviewsUrl = networker.buildReviewsUrl(id);
                try {
                    String JSONResponseTrailers = networker.getResponseFromHttpUrl(requestTrailersUrl);
                    mTrailers =  fetchTrailersFromJson(JSONResponseTrailers);

                    String JSONResponseReviews = networker.getResponseFromHttpUrl(requestReviewsUrl);
                    mReviews = fetchReviewsFromJson(JSONResponseReviews);

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        //set the views
        String forward = Trailer.arrayToString(mTrailers);
        Log.d(TAG,Review.arrayToString(mReviews));
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }


    private ArrayList<Trailer> fetchTrailersFromJson(String jsonString) throws JSONException {
        JSONObject json = new JSONObject(jsonString);
        JSONArray trailers = json.getJSONArray("results");
        ArrayList<Trailer> result = new ArrayList<>();

        for (int i = 0; i< trailers.length(); i++){
            JSONObject trailerObject = trailers.getJSONObject(i);
            String site = trailerObject.getString("site");
            if (site.equals("YouTube")){
                String url = "https://www.youtube.com/watch?v="+trailerObject.getString("key");
                result.add(new Trailer(trailerObject.getString("name"),url));
            }
        }
        Log.d(TAG,"Fethed Trailers");
        return result;
    }

    private ArrayList<Review> fetchReviewsFromJson(String jsonString) throws JSONException{
        JSONObject json = new JSONObject(jsonString);
        JSONArray trailers = json.getJSONArray("results");
        ArrayList<Review> result = new ArrayList<>();

        for (int i = 0; i< trailers.length(); i++){
            JSONObject trailerObject = trailers.getJSONObject(i);
            result.add(new Review(trailerObject.getString("author"),trailerObject.getString("content")));
        }
        Log.d(TAG,"Fetched Reviews");
        return result;
    }

}

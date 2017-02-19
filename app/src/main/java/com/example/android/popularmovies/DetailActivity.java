package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.database.MovieContract;
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
    ListView mTrailersListView;
    ImageButton mBookmarksButton;
    Button mReviewsButton;

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
        mTrailersListView = (ListView) findViewById(R.id.trailers_list_view);
        mBookmarksButton = (ImageButton) findViewById(R.id.bookmark_button);
        mReviewsButton = (Button) findViewById(R.id.reviews_button);


        Intent callerIntent = getIntent();
        if (callerIntent.hasExtra(Movie.EXTRA_MOVIE)){
            mMovie = new Movie(callerIntent.getBundleExtra(Movie.EXTRA_MOVIE));
            mTitle.setText(mMovie.title);
            mReleaseDate.setText(String.format(getString(R.string.release_date), mMovie.release_date));
            mAverage.setText(String.format(getString(R.string.vote_average), mMovie.vote_average));
            mPlot.setText(mMovie.overview);
            Bundle args = new Bundle();
            if (mMovie.isBookmarked(this)){
                mBookmarksButton.setImageResource(android.R.drawable.btn_star_big_on);
                Picasso.with(this).load(mMovie.getPosterUri(getString(R.string.poster_default_size))).into(mPoster);
                args.putBoolean("local",true);

            }else {
                mBookmarksButton.setImageResource(android.R.drawable.btn_star_big_off);
                Picasso.with(this).load(mMovie.getPosterUri(getString(R.string.poster_default_size))).into(mPoster);
                args.putBoolean("local",false);
            }

            getSupportLoaderManager().restartLoader(LOADER_ID, args, this);

            mBookmarksButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = getApplicationContext();
//                    Toast.makeText(context,"Pressed",Toast.LENGTH_SHORT).show();
                    if (!mMovie.isBookmarked(context)){
                        if(mMovie.saveToBookmarks(context)){
                            mBookmarksButton.setImageResource(android.R.drawable.btn_star_big_on);
                        }
                    }
                    else{
                        if(mMovie.removeFromBookmarks(context)){
                            mBookmarksButton.setImageResource(android.R.drawable.btn_star_big_off);
                        }
                    }
                }
            });
        }

    }

    @Override
    public Loader<Object> onCreateLoader(final int id, final Bundle args) {

        return new AsyncTaskLoader<Object>(this) {
            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public Void loadInBackground() {

                if (args != null && args.size() != 0) {
                    boolean local = args.getBoolean("local");
                    long id = mMovie.id;
                    if (!local) {
                        NetworkUtils networker = new NetworkUtils(getApplicationContext());
                        URL requestTrailersUrl = networker.buildTrailersUrl(id);
                        URL requestReviewsUrl = networker.buildReviewsUrl(id);
                        try {
                            String JSONResponseTrailers = networker.getResponseFromHttpUrl(requestTrailersUrl);
                            mTrailers = fetchTrailersFromJson(JSONResponseTrailers);

                            String JSONResponseReviews = networker.getResponseFromHttpUrl(requestReviewsUrl);
                            mReviews = fetchReviewsFromJson(JSONResponseReviews);

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.d(TAG, "Starting local query");
                        Cursor cursor = getContentResolver()
                                .query(MovieContract.MovieEntry.CONTENT_URI,
                                        new String[]{MovieContract.MovieEntry.MOVIE_TRAILERS, MovieContract.MovieEntry.MOVIE_REVIEWS},
                                        MovieContract.MovieEntry.MOVIE_ID + "=?",
                                        new String[]{Long.toString(id)}, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            Log.d(TAG, cursor.getString(0));
                            mTrailers = Trailer.stringToArray(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_TRAILERS)));
                            mReviews = Review.stringToArray(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_REVIEWS)));
                            cursor.close();
                        }

                    }
                }

                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        mMovie.setTrailers(mTrailers);
        mMovie.setReviews(mReviews);
        ArrayAdapter<String> trailersAdapter = new ArrayAdapter<String>(this,R.layout.trailer_list_item,Trailer.getTitles(mTrailers));
        mTrailersListView.setAdapter(trailersAdapter);
        setListViewHeightBasedOnChildren(mTrailersListView);

        mTrailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = Trailer.getUrls(mTrailers)[position];
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });


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

    public static void setListViewHeightBasedOnChildren(ListView listView) {

        ArrayAdapter listAdapter = (ArrayAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int elements = listAdapter.getCount();
        if (elements>0) {
            View listItem = listAdapter.getView(0, null, listView);
            listItem.measure(0,0);

            int totalHeight = listItem.getMeasuredHeight() * elements;

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight
                    + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
        }
    }
}

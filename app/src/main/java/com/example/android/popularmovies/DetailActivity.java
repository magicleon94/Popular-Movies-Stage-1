package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.popularmovies.database.MovieContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {
    private final String TAG = this.getClass().getSimpleName();
    Movie mMovie;
    @BindView(R.id.tv_details_title)
    TextView mTitle;
    @BindView(R.id.tv_details_release_date)
    TextView mReleaseDate;
    @BindView(R.id.tv_details_average)
    TextView mAverage;
    @BindView(R.id.tv_details_plot)
    TextView mPlot;
    @BindView(R.id.detail_poster)
    ImageView mPoster;
    @BindView(R.id.trailers_list_view)
    ListView mTrailersListView;
    @BindView(R.id.bookmark_button)
    ImageButton mBookmarksButton;

    ArrayList<Trailer> mTrailers;
    ArrayList<Review> mReviews;

    TrailersAdapter trailersAdapter;

    private static final int LOADER_ID = 818;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_constraint);
        ButterKnife.bind(this);
        trailersAdapter = new TrailersAdapter(this);
        mTrailersListView.setAdapter(trailersAdapter);

        Intent callerIntent = getIntent();
        if (callerIntent.hasExtra(Movie.EXTRA_MOVIE)){
            mMovie = new Movie(callerIntent.getBundleExtra(Movie.EXTRA_MOVIE));
            mTitle.setText(mMovie.title);
            mReleaseDate.setText(String.format(getString(R.string.release_date), mMovie.release_date));
            mAverage.setText(String.format(getString(R.string.vote_average), mMovie.vote_average));
            mPlot.setText(mMovie.overview);
            final Bitmap[] posterBitmap = new Bitmap[1];
            Bundle args = new Bundle();
            if (mMovie.isBookmarked(this)){
                mBookmarksButton.setImageResource(android.R.drawable.btn_star_big_on);
                args.putBoolean("local",true);

            }else {
                mBookmarksButton.setImageResource(android.R.drawable.btn_star_big_off);
                Picasso.with(this).load(mMovie.getPosterUri(getString(R.string.poster_default_size)))
                        .into(new Target(){
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                posterBitmap[0] = bitmap;
                                mMovie.setPoster(posterBitmap[0]);
                                mPoster.setImageBitmap(posterBitmap[0]);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }
                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                args.putBoolean("local",false);
            }
            mPoster.setImageBitmap(mMovie.getPoster());

            mTrailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Uri uri = trailersAdapter.getTrailerUri(position);

                    if (uri != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }
            });

            getSupportLoaderManager().restartLoader(LOADER_ID, args, this);

        }

    }

    @OnClick(R.id.bookmark_button)
    public void toggleBookmark(View v) {
        Context context = getApplicationContext();
        if (!mMovie.isBookmarked(context)) {
            if (mMovie.saveToBookmarks(context)) {
                mBookmarksButton.setImageResource(android.R.drawable.btn_star_big_on);
            }
        } else {
            if (mMovie.removeFromBookmarks(context)) {
                mBookmarksButton.setImageResource(android.R.drawable.btn_star_big_off);
            }
        }
    }

    @OnClick(R.id.reviews_button)
    public void seeReviews(View v) {
        String reviewsString = Review.arrayToString(mReviews);
        Intent reviewsIntent = new Intent(getApplicationContext(), ReviewsActivity.class);
        reviewsIntent.putExtra(getString(R.string.reviews_intent_extra), reviewsString);
        startActivity(reviewsIntent);
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
                        Log.d(TAG, "Starting online query");
                        NetworkUtils networker = new NetworkUtils();
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
                                        new String[]{MovieContract.MovieEntry.MOVIE_TRAILERS, MovieContract.MovieEntry.MOVIE_REVIEWS, MovieContract.MovieEntry.MOVIE_POSTER},
                                        MovieContract.MovieEntry.MOVIE_ID + "=?",
                                        new String[]{Long.toString(id)}, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            Log.d(TAG, cursor.getString(0));
                            mTrailers = Trailer.stringToArray(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_TRAILERS)));
                            mReviews = Review.stringToArray(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_REVIEWS)));
                            mMovie.setPosterFromCursor(cursor);
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
        mPoster.setImageBitmap(mMovie.getPoster());
        if (mTrailers!=null){
            trailersAdapter.setTrailers(mTrailers);
            setListViewHeightBasedOnChildren(mTrailersListView);
        }

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

    public void setListViewHeightBasedOnChildren(ListView listView) {

        TrailersAdapter listAdapter = (TrailersAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int elements = listAdapter.getCount();
        Log.d("AAA","Got " + elements + " elements");

        if (elements>0) {
            View listItem = listAdapter.getView(0, null, listView);
            listItem.measure(0,0);
            // get the height of a single item, multiply by the number of items and get the total height for the item,
            // extra space (more elements) is added
            int totalHeight = listItem.getMeasuredHeight() * (elements+2);

            ViewGroup.LayoutParams params = listView.getLayoutParams();

            //calculate the total height summing the height of the dividers too
            params.height = totalHeight
                    + (listView.getDividerHeight() * (listAdapter.getCount()-1));

            //set the height
            listView.setLayoutParams(params);
        }
    }
}

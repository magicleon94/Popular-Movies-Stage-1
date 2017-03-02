package com.example.android.popularmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.database.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements PostersAdapter.onPosterClickHandler,LoaderManager.LoaderCallbacks<ArrayList<Movie>> {
    @BindView(R.id.rv_posters)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_posters_error)
    TextView mErrorTextView;
    @BindView(R.id.tv_posters_no_bookmarks)
    TextView mNoBookmarksTextView;
    @BindView(R.id.posters_progress_bar)
    ProgressBar mProgressBar;
    @BindString(R.string.movie_preferences)
    String MOVIE_PREFERENCES;
    @BindString(R.string.pref_sorting_key)
    String MOVIE_SORTING;
    @BindString(R.string.pref_sorting_popular)
    String MOVIE_SORTING_POPULAR;
    @BindString(R.string.pref_bookmarked)
    String MOVIE_BOOKMARKED;
    PostersAdapter mPostersAdapter;
    final String TAG = MainActivity.class.getSimpleName();
    int mPagesLoaded;
    final int MAX_PAGES = 20;
    GridLayoutManager mGridLayoutManager;

    AlertDialog mSortingDialog;
    SharedPreferences mSharedPrefs;
    SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener;
    String actualCriterion;

    private static final int LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mGridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        } else {
            mGridLayoutManager = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false);
        }

        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mPostersAdapter = new PostersAdapter(this);

        mRecyclerView.setAdapter(mPostersAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!actualCriterion.equals(MOVIE_BOOKMARKED)) {
                    int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                    int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                    int pastVisiblesItems = mGridLayoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loadPosters();
                    }
                }
            }
        });

        mPagesLoaded = 0;

        mSortingDialog = initSortingDialog();

        initSharedPreferences();
        if (savedInstanceState != null) {
            Log.d(TAG, "Restoring adapter");
            mPostersAdapter.restoreInstanceState(savedInstanceState);
            Log.d(TAG,mPostersAdapter.getItemCount() + " items recovered");
            mRecyclerView.scrollToPosition(savedInstanceState.getInt("SCROLL_POSITION"));
        } else {
            loadPosters();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Saving instance state");
        mPostersAdapter.saveInstanceState(outState);
        int scrollPosition = mGridLayoutManager.findFirstVisibleItemPosition();
        outState.putInt("SCROLL_POSITION", scrollPosition);

    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mNoBookmarksTextView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }
    private void showNoBookmarksMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
        mNoBookmarksTextView.setVisibility(View.VISIBLE);
    }
    private void showPosters() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
        mNoBookmarksTextView.setVisibility(View.INVISIBLE);

    }
    private void loadPosters() {
        Log.d(TAG,"Loading posters");
        if (mPagesLoaded < MAX_PAGES) {
            Bundle args = new Bundle();
            args.putInt("page",mPagesLoaded+1);
            getSupportLoaderManager().restartLoader(LOADER_ID,args,this);
        }
    }

    private AlertDialog initSortingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Sorting criteria");
        builder.setItems(R.array.pref_sorting_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] criteria = getResources().getStringArray(R.array.pref_sorting_values);
                SharedPreferences.Editor editor = mSharedPrefs.edit();
                editor.putString(MOVIE_SORTING, criteria[which]);
                editor.apply();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("AAA", "Canceled");
            }
        });
        return builder.create();
    }
    private void initSharedPreferences() {
        mSharedPrefs = getApplicationContext().getSharedPreferences("movie_preferences", MODE_PRIVATE);
        mOnSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                actualCriterion = sharedPreferences.getString(key, getString(R.string.pref_sorting_popular));
                Log.d(TAG, "Shared preferences for " + key + "changed. Pref: " + actualCriterion);
                mPagesLoaded = 0;
                mPostersAdapter.clear();
                loadPosters();
            }
        };
        mSharedPrefs.registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
        Log.d(TAG, MOVIE_PREFERENCES);
        actualCriterion = mSharedPrefs.getString(MOVIE_SORTING, MOVIE_BOOKMARKED);

    }

    @Override
    public void onClick(Movie movie) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra(Movie.EXTRA_MOVIE, movie.toBundle());
        startActivity(detailIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sorting_criteria) {
            if (mSortingDialog != null) {
                mSortingDialog.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<Movie>>(this) {
            ArrayList<Movie> mData;

            @Override
            protected void onStartLoading() {
                Log.d(TAG,"Start Loading");
                super.onStartLoading();
                if (actualCriterion.equals(MOVIE_BOOKMARKED)) {
                    //force refresh
                    mPostersAdapter.clear();
                    forceLoad();
                }
                else {
                    if (mData != null) {
                        deliverResult(mData);
                    } else {
                        if (mPagesLoaded == 0) {
                            mProgressBar.setVisibility(View.VISIBLE);
                        }
                        mErrorTextView.setVisibility(View.INVISIBLE);
                        forceLoad();
                    }
                }
            }

            @Override
            public ArrayList<Movie> loadInBackground() {
                Log.d(TAG,"Load in background");
                if (args.size() == 0) {
                    return null;
                }
                int page = args.getInt("page");
                NetworkUtils networker = new NetworkUtils();
                if (!(actualCriterion.equals(MOVIE_BOOKMARKED))) {
                    URL request = networker.buildMoviesUrl(page, actualCriterion);
                    try {
                        String JSONResponse = networker.getResponseFromHttpUrl(request);
                        ArrayList<Movie> res =  fetchMoviesFromJson(JSONResponse);
                        mPagesLoaded++;
                        return res;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
                else{
                    Log.d(TAG,"Local Loading");
                    Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,null,null,null,null);
                    if (cursor!=null){
                        Log.d(TAG,"Cursor is not null");
                        ArrayList<Movie> res = fetchMoviesFromCursor(cursor);
                        cursor.close();
                        return res;
                    }
                    return null;
                }

            }

            @Override
            public void deliverResult(ArrayList<Movie> data) {
                mData = data;
                mProgressBar.setVisibility(View.INVISIBLE);
                super.deliverResult(data);
            }
        };
    }
    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
        Log.d(TAG,"Load finished");
        mProgressBar.setVisibility(View.INVISIBLE);
        if (movies != null) {
            mPostersAdapter.addMovies(movies);
            Log.d(TAG,mPostersAdapter.getItemCount() + " items loaded");
            showPosters();
        } else {
            if (actualCriterion.equals(MOVIE_BOOKMARKED)) {
                showNoBookmarksMessage();
            }else {
                showErrorMessage();
            }
        }
    }
    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        Log.d(TAG,"Restarting loader");

    }

    private ArrayList<Movie> fetchMoviesFromJson(String jsonStr) throws JSONException {
        final String KEY_MOVIES = "results";

        JSONObject json = new JSONObject(jsonStr);
        JSONArray movies = json.getJSONArray(KEY_MOVIES);
        ArrayList<Movie> result = new ArrayList<>();

        for (int i = 0; i < movies.length(); i++) {
            Movie resMovie = Movie.getMovieFromJson(movies.getJSONObject(i));
            result.add(resMovie);
        }
        return result;
    }
    private ArrayList<Movie> fetchMoviesFromCursor(Cursor cursor){
        ArrayList<Movie> result = new ArrayList<>();
        Log.d(TAG,"Found" + cursor.getCount() + " bookmarks");

        if (cursor.getCount()==0){
            return null;
        }
        if(cursor.moveToFirst()){
            do{
                Movie movie = new Movie(
                        cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_ID)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_TITLE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_OVERVIEW)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_POSTER_PATH)),
                        cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_AVG)),
                        cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_VOTES)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_RELEASE_DATE))
                );

                movie.setPosterFromCursor(cursor);

                result.add(movie);

            }while(cursor.moveToNext());

        }

        return result;
    }
}
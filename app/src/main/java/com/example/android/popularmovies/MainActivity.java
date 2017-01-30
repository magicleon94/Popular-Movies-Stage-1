package com.example.android.popularmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PostersAdapter.onPosterClickHandler {
    final String TAG = MainActivity.class.getSimpleName();
    RecyclerView mRecyclerView;
    TextView mErrorTextView;
    PostersAdapter mPostersAdapter;
    ProgressBar mProgressBar;
    int mPagesLoaded;
    final int MAX_PAGES = 20;
    GridLayoutManager mGridLayoutManager;

    AlertDialog mSortingDialog;
    SharedPreferences mSharedPrefs;
    SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_posters);
        mErrorTextView = (TextView) findViewById(R.id.tv_posters_error);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mGridLayoutManager = new GridLayoutManager(this,2, LinearLayoutManager.VERTICAL,false);
        }else{
            mGridLayoutManager = new GridLayoutManager(this,4, LinearLayoutManager.VERTICAL,false);
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

                int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                int pastVisiblesItems = mGridLayoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    loadPosters();
                }
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.posters_progress_bar);

        mPagesLoaded = 0;

        mSortingDialog = initSortingDialog();

        initSharedPreferences();
        if (savedInstanceState!=null){
            Log.d(TAG,"Restoring adapter");
            mPostersAdapter.restoreInstanceState(savedInstanceState);
            mRecyclerView.scrollToPosition(savedInstanceState.getInt("SCROLL_POSITION"));
        }else{
            loadPosters();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"Saving instance state");
        mPostersAdapter.saveInstanceState(outState);
        int scrollPosition = mGridLayoutManager.findFirstVisibleItemPosition();
        outState.putInt("SCROLL_POSITION",scrollPosition);

    }

    private void showErrorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    private void showPosters(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
    }

    private void loadPosters(){
        if (mPagesLoaded<MAX_PAGES){
            new MovieFetcher().execute(mPagesLoaded+1);
        }
    }

    @Override
    public void onClick(Movie movie) {
        Intent detailIntent = new Intent(this,DetailActivity.class);
        detailIntent.putExtra(Movie.EXTRA_MOVIE,movie.toBundle());
        startActivity(detailIntent);
//        Toast.makeText(this,movie.title,Toast.LENGTH_SHORT).show();
    }

    private AlertDialog initSortingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Sorting criteria");
        builder.setItems(R.array.pref_sorting_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] criteria = getResources().getStringArray(R.array.pref_sorting_values);
//                Log.d(TAG,criteria[which]);
                SharedPreferences.Editor editor = mSharedPrefs.edit();
                editor.putString("sorting",criteria[which]);
                editor.apply();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("AAA","Canceled");
            }
        });
        return builder.create();
    }

    private void initSharedPreferences(){
        mSharedPrefs = getApplicationContext().getSharedPreferences("movie_preferences",MODE_PRIVATE);
        mOnSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d(TAG,"Shared preferences for " + key + "changed. Pref: " + sharedPreferences.getString(key,null));
                mPagesLoaded = 0;
                mPostersAdapter.clear();
                loadPosters();
            }
        };
        mSharedPrefs.registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);

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
            if (mSortingDialog!=null){
                mSortingDialog.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private class MovieFetcher extends AsyncTask<Integer,Void,ArrayList<Movie>> {
        @Override
        protected ArrayList<Movie> doInBackground(Integer... params) {
            if (params.length==0){
                return null;
            }
            int page = params[0];
            NetworkUtils networker = new NetworkUtils(getApplicationContext());
            URL request = networker.buildUrl(page);
            try {
                String JSONResponse = networker.getResponseFromHttpUrl(request);
                return fetchMoviesFromJson(JSONResponse);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mPagesLoaded == 0){
                mProgressBar.setVisibility(View.VISIBLE);
            }
            mErrorTextView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (movies!=null) {
                mPostersAdapter.addMovies(movies);
                mPagesLoaded++;
                showPosters();
            }else{
                showErrorMessage();
            }
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
    }
}
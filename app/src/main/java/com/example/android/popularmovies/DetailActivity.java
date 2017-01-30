package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    TextView mTitle;
    TextView mReleaseDate;
    TextView mAverage;
    TextView mPlot;
    ImageView mPoster;

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
            Movie movie = new Movie(callerIntent.getBundleExtra(Movie.EXTRA_MOVIE));
            mTitle.setText(movie.title);
            mReleaseDate.setText(String.format(getString(R.string.release_date), movie.release_date));
            mAverage.setText(String.format(getString(R.string.vote_average), movie.vote_average));
            mPlot.setText(movie.overview);
            Picasso.with(this).load(movie.getPosterUri(getString(R.string.poster_default_size))).into(mPoster);
        }

    }
}

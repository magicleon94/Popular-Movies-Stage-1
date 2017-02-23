package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by magicleon on 22/02/17.
 */

public class ReviewsActivity extends AppCompatActivity {
    ListView reviewsListView;
    ReviewsAdapter mAdapter;
    TextView mErrorTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        reviewsListView = (ListView) findViewById(R.id.reviews_listView);
        mErrorTextView = (TextView) findViewById(R.id.tv_reviews_error);
        mAdapter = new ReviewsAdapter(this);
        Intent intent = getIntent();

        reviewsListView.setAdapter(mAdapter);

        if (intent.hasExtra(getString(R.string.reviews_intent_extra))){
            ArrayList<Review> reviews = Review.stringToArray(intent.getStringExtra(getString(R.string.reviews_intent_extra)));

            if (reviews.size()>0) {
                mAdapter.setReviews(reviews);
            }else{
                Log.d("AAA","Showing error message");
                reviewsListView.setVisibility(View.GONE);
                mErrorTextView.setVisibility(View.VISIBLE);
            }

        }
    }
}

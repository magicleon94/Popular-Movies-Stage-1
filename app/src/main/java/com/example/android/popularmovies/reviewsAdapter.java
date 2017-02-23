package com.example.android.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by magicleon on 23/02/17.
 */

public class ReviewsAdapter extends BaseAdapter {
    ArrayList<Review> mReviews;
    Context context;
    public ReviewsAdapter(Context context){
        this.context = context;
        mReviews = new ArrayList<>();
    }

    public void setReviews(ArrayList<Review> data){
        mReviews.clear();
        mReviews.addAll(data);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mReviews.size();
    }

    @Override
    public Review getItem(int position) {
        if (position>=0 && position<mReviews.size()){
            return mReviews.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (getItem(position) == null){
            return -1L;
        }
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View reviewItem = convertView;
        Review review = getItem(position);
        if(reviewItem==null){
            try{
                LayoutInflater vi;
                vi = LayoutInflater.from(context);
                reviewItem = vi.inflate(R.layout.review_list_item,null);

            }catch (Exception e){
                Log.e(context.getClass().getSimpleName(),e.toString());
            }
        }
        ((TextView) reviewItem.findViewById(R.id.review_item_author)).setText("Author: " + review.author);
        ((TextView) reviewItem.findViewById(R.id.review_item_content)).setText(review.content);
        return  reviewItem;
    }
}

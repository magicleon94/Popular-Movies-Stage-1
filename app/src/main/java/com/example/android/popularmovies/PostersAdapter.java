package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

class PostersAdapter extends RecyclerView.Adapter<PostersAdapter.PosterViewHolder> {
    private static final String TAG = PostersAdapter.class.getSimpleName();
    private ArrayList<Movie> mMovies;
    private final onPosterClickHandler mClickHandler;

    interface onPosterClickHandler{
        void onClick(Movie movie);

    }

    PostersAdapter(onPosterClickHandler clickHandler){
        mMovies = new ArrayList<>();
        mClickHandler = clickHandler;
    }

    void addMovies(ArrayList<Movie> movies){
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    void clear(){
        mMovies.clear();
        notifyDataSetChanged();
    }

    void saveInstanceState(Bundle outState){
        outState.putParcelableArrayList("ADAPTER_MOVIES",mMovies);
    }

    void restoreInstanceState(Bundle savedInstanceState){
        Log.d(TAG,"Restoring called");
        if (savedInstanceState.containsKey("ADAPTER_MOVIES")){
            ArrayList<Movie> savedMovies = savedInstanceState.getParcelableArrayList("ADAPTER_MOVIES");
            mMovies.clear();
            mMovies.addAll(savedMovies);
            notifyDataSetChanged();
        }
    }
    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        int layoutItemId = R.layout.poster_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutItemId,parent,false);

        return new PosterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        holder.setImage(mMovies.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.item_poster_image)
        ImageView mImageView;
        Context mContext;

        PosterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }

        void setImage(Movie movie){
            Uri posterUri = movie.getPosterUri(mContext.getString(R.string.poster_default_size));
            Picasso.with(mContext).load(posterUri).into(mImageView);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Movie selectedMovie = mMovies.get(position);
            mClickHandler.onClick(selectedMovie);
        }
    }
}

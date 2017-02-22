package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.android.popularmovies.Trailer;

import java.util.ArrayList;

/**
 * Created by magicleon on 22/02/17.
 */

public class TrailersAdapter extends BaseAdapter {
    Context context;
    ArrayList<Trailer> mTrailers;

    public TrailersAdapter(Context context){
        this.context = context;
        this.mTrailers = new ArrayList<>();
    }

    public void clear(){
        mTrailers.clear();
        notifyDataSetChanged();
    }

    public void setTrailers(ArrayList<Trailer> trailers){
        clear();
        mTrailers.addAll(trailers);
        notifyDataSetChanged();
    }

    public void addTrailers(ArrayList<Trailer> trailers){
        mTrailers.addAll(trailers);
        notifyDataSetChanged();
    }
    public Uri getTrailerUri(int position){
        Trailer trailer = getItem(position);
        if (trailer!=null){
            return Uri.parse(trailer.url);
        }
        return null;
    }
    @Override
    public int getCount() {
        return mTrailers.size();
    }

    @Override
    public Trailer getItem(int position) {
        if (position>=0 && position<mTrailers.size()){
            return mTrailers.get(position);
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
        View trailerItem = convertView;
        Trailer trailer = getItem(position);
        if(trailerItem==null){
            try{
                LayoutInflater vi;
                vi = LayoutInflater.from(context);
                trailerItem = vi.inflate(R.layout.trailer_list_item,null);

            }catch (Exception e){
                Log.e(context.getClass().getSimpleName(),e.toString());
            }
        }
        ((TextView) trailerItem.findViewById(R.id.tv_trailer_item_title)).setText(trailer.title);
        return  trailerItem;
    }
}

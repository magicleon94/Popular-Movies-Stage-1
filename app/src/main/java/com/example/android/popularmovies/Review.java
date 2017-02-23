package com.example.android.popularmovies;

import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by magicleon on 18/02/17.
 */

public class Review{
    String author;
    String content;
    final String TAG = getClass().getSimpleName();

    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }
    public static String arrayToString(ArrayList<Review> reviews){
        String res = "";
        try {
            for (int i = 0; i < reviews.size(); i++) {
                res += reviews.get(i).author + ",lullo," + reviews.get(i).content;
                if (i < reviews.size() - 1) {
                    res += " -lullo- ";
                }
            }
        }catch (NullPointerException e){
            return "";
        }
        return res;
    }

    public static ArrayList<Review> stringToArray(String string){
        String[] elements = string.split(" -lullo- ");
        ArrayList<Review> res = new ArrayList<>();

        for (String element : elements) {
            String[] item = element.split(",lullo,");
            try{
                res.add(new Review(item[0], item[1]));
            }catch (IndexOutOfBoundsException e){
                Log.d("REVIEWS",e.toString());
                continue;
            }
        }
        return res;
    }
}
package com.example.android.popularmovies;

import android.util.Log;

import java.util.ArrayList;


class Review{
    String author;
    String content;

    Review(String author, String content) {
        this.author = author;
        this.content = content;
    }
    static String arrayToString(ArrayList<Review> reviews){
        String res = "";
        try {
            for (int i = 0; i < reviews.size(); i++) {
                res += reviews.get(i).author + ",reviewSeparator," + reviews.get(i).content;
                if (i < reviews.size() - 1) {
                    res += " -reviewSeparator- ";
                }
            }
        }catch (NullPointerException e){
            return "";
        }
        return res;
    }

    static ArrayList<Review> stringToArray(String string){
        String[] elements = string.split(" -reviewSeparator- ");
        ArrayList<Review> res = new ArrayList<>();

        for (String element : elements) {
            String[] item = element.split(",reviewSeparator,");
            try{
                res.add(new Review(item[0], item[1]));
            }catch (IndexOutOfBoundsException e){
                Log.d("REVIEWS",e.toString());
            }
        }
        return res;
    }
}
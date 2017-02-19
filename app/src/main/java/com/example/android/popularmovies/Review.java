package com.example.android.popularmovies;

import java.util.ArrayList;

/**
 * Created by magicleon on 18/02/17.
 */

public class Review{
    String author;
    String content;

    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }
    public static String arrayToString(ArrayList<Review> reviews){
        String res = "";
        try {
            for (int i = 0; i < reviews.size(); i++) {
                res += reviews.get(i).author + "," + reviews.get(i).content;
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
            String[] item = element.split(",");
            res.add(new Review(item[0], item[1]));
        }

        return res;
    }
}
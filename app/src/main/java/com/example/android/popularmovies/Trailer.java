package com.example.android.popularmovies;

import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by magicleon on 18/02/17.
 */
public class Trailer{
    String title;
    String url;

    public Trailer(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public static String arrayToString(ArrayList<Trailer> trailers){
        String res = "";
        try {
            for (int i = 0; i < trailers.size(); i++) {
                res += trailers.get(i).title + "," + trailers.get(i).url;
                if (i < trailers.size() - 1) {
                    res += " -lullo- ";
                }
            }
        }catch (NullPointerException e){
            return "";
        }
        return res;
    }

    public static ArrayList<Trailer> stringToArray(String string){
        String[] elements = string.split(" -lullo- ");

        ArrayList<Trailer> res = new ArrayList<>();

        for (String element : elements) {
            String[] item = element.split(",");
            res.add(new Trailer(item[0], item[1]));
        }

        return res;
    }

    public static String[] getTitles(ArrayList<Trailer> trailers){
        String[] titles = new String[trailers.size()];
        for (int i=0; i<trailers.size(); i++){
            titles[i] = trailers.get(i).title;
        }
        return titles;
    }

    public static String[] getUrls(ArrayList<Trailer> trailers){
        String[] titles = new String[trailers.size()];
        for (int i=0; i<trailers.size(); i++){
            titles[i] = trailers.get(i).url;
        }

        return titles;
    }
}
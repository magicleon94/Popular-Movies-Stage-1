package com.example.android.popularmovies;

import java.util.ArrayList;

class Trailer{
    String title;
    String url;

    Trailer(String title, String url) {
        this.title = title;
        this.url = url;
    }

    static String arrayToString(ArrayList<Trailer> trailers){
        String res = "";
        try {
            for (int i = 0; i < trailers.size(); i++) {
                res += trailers.get(i).title + "," + trailers.get(i).url;
                if (i < trailers.size() - 1) {
                    res += " -trailerSeparator- ";
                }
            }
        }catch (NullPointerException e){
            return "";
        }
        return res;
    }

    static ArrayList<Trailer> stringToArray(String string){
        String[] elements = string.split(" -trailerSeparator- ");

        ArrayList<Trailer> res = new ArrayList<>();

        for (String element : elements) {
            String[] item = element.split(",");
            res.add(new Trailer(item[0], item[1]));
        }

        return res;
    }

}
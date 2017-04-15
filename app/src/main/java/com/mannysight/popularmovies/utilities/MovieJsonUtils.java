package com.mannysight.popularmovies.utilities;

import com.google.gson.Gson;
import com.mannysight.popularmovies.apimodel.MovieList;
import com.mannysight.popularmovies.apimodel.Result;

/**
 * Created by wamuo on 4/15/2017.
 */

public class MovieJsonUtils {
    public static MovieList getMovieListObjectFromJson(String jsonString) {
        Gson gson = new Gson();
        MovieList movieList = gson.fromJson(jsonString, MovieList.class);
        return movieList;
    }

    public static String serializeMovieToJson(Result movie) {
        Gson gson = new Gson();
        String j = gson.toJson(movie);
        return j;
    }

    public static Result deserializeFromJson(String jsonMovieString) {
        Gson gson = new Gson();
        Result movie = gson.fromJson(jsonMovieString, Result.class);
        return movie;
    }

}
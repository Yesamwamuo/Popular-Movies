package com.mannysight.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.mannysight.popularmovies.apimodel.MovieList;
import com.mannysight.popularmovies.apimodel.Result;
import com.mannysight.popularmovies.data.MoviePreferences;
import com.mannysight.popularmovies.utilities.MovieJsonUtils;
import com.mannysight.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by wamuo on 4/17/2017.
 */

public class FetchMovieTask extends AsyncTask<MoviePreferences, Void, ArrayList<Result>> {

    private final Context ctx;
    private final AsyncTaskActionListener<ArrayList<Result>> listener;

    public FetchMovieTask(Context ctx, AsyncTaskActionListener<ArrayList<Result>> listener) {
        this.ctx = ctx;
        this.listener = listener;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onTaskBegin();

    }

    @Override
    protected ArrayList<Result> doInBackground(MoviePreferences... moviePreferences) {
        if (moviePreferences.length == 0) {
            return null;
        }
        MoviePreferences preference = moviePreferences[0];
        URL movieRequestUrl = NetworkUtils.buildUrl(preference);

        try {
            String jsonMovieResponse = NetworkUtils
                    .getResponseFromHttpUrl(movieRequestUrl);

            MovieList movieListData = MovieJsonUtils
                    .getMovieListObjectFromJson(jsonMovieResponse);

            return (ArrayList<Result>) movieListData.getResults();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(ArrayList<Result> movies) {
        super.onPostExecute(movies);
       listener.onTaskComplete(movies);
    }
}


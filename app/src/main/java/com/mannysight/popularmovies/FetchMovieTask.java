package com.mannysight.popularmovies;

import android.os.AsyncTask;

import com.mannysight.popularmovies.apimodel.MovieList;
import com.mannysight.popularmovies.apimodel.Result;
import com.mannysight.popularmovies.utilities.MovieJsonUtils;
import com.mannysight.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by wamuo on 4/17/2017.
 */

public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Result>> {

    private final AsyncTaskActionListener<ArrayList<Result>> listener;

    public FetchMovieTask(AsyncTaskActionListener<ArrayList<Result>> listener) {
        this.listener = listener;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onTaskBegin();

    }

    @Override
    protected ArrayList<Result> doInBackground(String... preferences) {
        if (preferences.length == 0) {
            return null;
        }
        String preference = preferences[0];
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


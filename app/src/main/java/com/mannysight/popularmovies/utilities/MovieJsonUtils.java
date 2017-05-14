package com.mannysight.popularmovies.utilities;

import com.google.gson.Gson;
import com.mannysight.popularmovies.apimodel.Results;
import com.mannysight.popularmovies.apimodel.ResultsForReview;
import com.mannysight.popularmovies.apimodel.ResultsForVideo;

/**
 * Created by wamuo on 4/15/2017.
 */

public class MovieJsonUtils {
    public static Results getResultListObjectFromJson(String jsonString) {
        Gson gson = new Gson();
        Results results = gson.fromJson(jsonString, Results.class);
        return results;
    }

    public static ResultsForReview getResultForReviewObjectFromJson(String jsonString) {
        Gson gson = new Gson();
        ResultsForReview results = gson.fromJson(jsonString, ResultsForReview.class);
        return results;
    }
    public static ResultsForVideo getResultForVideoObjectFromJson(String jsonString) {
        Gson gson = new Gson();
        ResultsForVideo results = gson.fromJson(jsonString, ResultsForVideo.class);
        return results;
    }



}

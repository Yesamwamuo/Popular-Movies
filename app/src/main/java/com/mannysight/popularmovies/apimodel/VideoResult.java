package com.mannysight.popularmovies.apimodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by wamuo on 5/7/2017.
 */

public class VideoResult {
    @SerializedName("key")
    @Expose
    private String key;

    public String getKey() {
        return key;
    }

}

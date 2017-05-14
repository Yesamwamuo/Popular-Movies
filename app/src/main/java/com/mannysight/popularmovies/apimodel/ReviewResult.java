package com.mannysight.popularmovies.apimodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by wamuo on 5/7/2017.
 */

public class ReviewResult {

    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("content")
    @Expose
    private String content;

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

}


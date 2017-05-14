package com.mannysight.popularmovies.apimodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wamuo on 5/7/2017.
 */

public class ResultsForReview implements Parcelable {

    @SerializedName("results")
    @Expose
    private List<ReviewResult> results = null;

    public List<ReviewResult> getResults() {
        return results;
    }

    public ResultsForReview() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.results);
    }

    protected ResultsForReview(Parcel in) {
        this.results = new ArrayList<ReviewResult>();
        in.readList(this.results, ReviewResult.class.getClassLoader());
    }

    public static final Parcelable.Creator<ResultsForReview> CREATOR = new Parcelable.Creator<ResultsForReview>() {
        @Override
        public ResultsForReview createFromParcel(Parcel source) {
            return new ResultsForReview(source);
        }

        @Override
        public ResultsForReview[] newArray(int size) {
            return new ResultsForReview[size];
        }
    };
}
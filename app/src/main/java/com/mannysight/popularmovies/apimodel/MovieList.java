package com.mannysight.popularmovies.apimodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wamuo on 4/15/2017.
 */


public class MovieList implements Parcelable {

    @SerializedName("results")
    @Expose
    private List<Result> results = null;

    public List<Result> getResults() {
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.results);
    }

    public MovieList() {
    }

    protected MovieList(Parcel in) {
        this.results = in.createTypedArrayList(Result.CREATOR);
    }

    public static final Parcelable.Creator<MovieList> CREATOR = new Parcelable.Creator<MovieList>() {
        @Override
        public MovieList createFromParcel(Parcel source) {
            return new MovieList(source);
        }

        @Override
        public MovieList[] newArray(int size) {
            return new MovieList[size];
        }
    };
}
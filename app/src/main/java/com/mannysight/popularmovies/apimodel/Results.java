package com.mannysight.popularmovies.apimodel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wamuo on 4/15/2017.
 */


public class Results implements Parcelable {

    @SerializedName("results")
    @Expose
    private List<MovieResult> results = null;

    public List<MovieResult> getResults() {
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

    public Results() {
    }

    protected Results(Parcel in) {
        this.results = in.createTypedArrayList(MovieResult.CREATOR);
    }

    public static final Parcelable.Creator<Results> CREATOR = new Parcelable.Creator<Results>() {
        @Override
        public Results createFromParcel(Parcel source) {
            return new Results(source);
        }

        @Override
        public Results[] newArray(int size) {
            return new Results[size];
        }
    };
}
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

public class ResultsForVideo implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<VideoResult> results = null;

    public List<VideoResult> getResults() {
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeList(this.results);
    }

    public ResultsForVideo() {
    }

    protected ResultsForVideo(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.results = new ArrayList<VideoResult>();
        in.readList(this.results, VideoResult.class.getClassLoader());
    }

    public static final Parcelable.Creator<ResultsForVideo> CREATOR = new Parcelable.Creator<ResultsForVideo>() {
        @Override
        public ResultsForVideo createFromParcel(Parcel source) {
            return new ResultsForVideo(source);
        }

        @Override
        public ResultsForVideo[] newArray(int size) {
            return new ResultsForVideo[size];
        }
    };
}


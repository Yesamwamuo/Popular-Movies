package com.mannysight.popularmovies;

/**
 * Created by wamuo on 4/17/2017.
 */

public interface AsyncTaskActionListener<T> {

    public void onTaskBegin();

    public void onTaskComplete(T result);
}

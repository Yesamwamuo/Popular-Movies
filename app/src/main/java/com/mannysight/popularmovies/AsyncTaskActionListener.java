package com.mannysight.popularmovies;

/**
 * Created by wamuo on 4/17/2017.
 */

interface AsyncTaskActionListener<T> {

    void onTaskBegin();

    void onTaskComplete(T result);
}

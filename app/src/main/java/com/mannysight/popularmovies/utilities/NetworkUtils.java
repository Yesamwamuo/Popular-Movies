package com.mannysight.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import com.mannysight.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by wamuo on 4/15/2017.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie";

    public static final String POPULAR_MOVIES =
            "http://api.themoviedb.org/3/movie/popular";

    public static final String TOP_RATED_MOVIES =
            "http://api.themoviedb.org/3/movie/top_rated";

    public static final String FAVOURITE_MOVIES = "saved_favourite_movies";

    public static final String REVIEW_PATH = "reviews";
    public static final String VIDEO_PATH = "videos";


    // TODO (1) Change API_KEY
    private final static String API_KEY = BuildConfig.THE_MOVIE_DB_API_TOKEN;
    private final static String API_KEY_PARAM = "api_key";
    private final static String PAGE_PARAM = "page";
    private final static String LANGUAGE_PARAM = "language";
    private static String MOVIE_DETAIL_PATH = "";


    public static URL buildMoviesUrl(String preference) {
        String baseUrl = "";
        switch (preference) {
            case POPULAR_MOVIES:
                baseUrl = POPULAR_MOVIES;
                break;
            case TOP_RATED_MOVIES:
                baseUrl = TOP_RATED_MOVIES;
                break;
        }
        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(LANGUAGE_PARAM, "en-US")
                .appendQueryParameter(PAGE_PARAM, Integer.toString(1))
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildMovieDetailUrl(String detail, int movieId) {
        switch (detail) {
            case REVIEW_PATH:
                MOVIE_DETAIL_PATH = REVIEW_PATH;
                break;
            case VIDEO_PATH:
                MOVIE_DETAIL_PATH = VIDEO_PATH;
                break;
        }
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(MOVIE_DETAIL_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(20000);
        urlConnection.setReadTimeout(20000);
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}

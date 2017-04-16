package com.mannysight.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.mannysight.popularmovies.data.MoviePreferences;

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

    private static final String POPULAR_MOVIES =
            "http://api.themoviedb.org/3/movie/popular";

    private static final String TOP_RATED_MOVIES =
            "http://api.themoviedb.org/3/movie/top_rated";

    private static String MOVIE_BASE_URL = "";

    // TODO (1) Change API_KEY
    private final static String API_KEY = "api_key goes here";

    private final static String API_KEY_PARAM = "api_key";
    private final static String PAGE_PARAM = "page";
    private final static String LANGUAGE_PARAM = "language";


    public static URL buildUrl(MoviePreferences preferences) {
        switch (preferences) {
            case POPULAR:
                MOVIE_BASE_URL = POPULAR_MOVIES;
                break;
            case TOP_RATED:
                MOVIE_BASE_URL = TOP_RATED_MOVIES;
                break;
        }
        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
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

    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}

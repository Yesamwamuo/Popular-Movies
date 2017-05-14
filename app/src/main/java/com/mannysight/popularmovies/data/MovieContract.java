package com.mannysight.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by wamuo on 4/29/2017.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.mannysight.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVOURITE = "favourites";

        private MovieContract() {
    }

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVOURITE)
                .build();

        public static final String TABLE_NAME = "favourites";
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_POSTER_PATH = "moviePosterPath";
        public static final String COLUMN_MOVIE_AVERAGE = "movieAverage";
        public static final String COLUMN_MOVIE_OVERVIEW = "movieOverview";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movieReleaseDate";
        public static final String COLUMN_MOVIE_API_ID = "movieId";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}

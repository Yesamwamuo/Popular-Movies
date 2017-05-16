package com.mannysight.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mannysight.popularmovies.apimodel.MovieResult;
import com.mannysight.popularmovies.apimodel.Results;
import com.mannysight.popularmovies.data.MovieContract;
import com.mannysight.popularmovies.utilities.MovieJsonUtils;
import com.mannysight.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, LoaderManager.LoaderCallbacks<ArrayList<MovieResult>>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int MOVIE_LIST_LOADER = 11;
    private static final String PREFERENCE_STRING_EXTRA = "PREFERENCE_STRING_EXTRA";
    private static final String ERROR1 = "ERROR1";
    private static final String ERROR2 = "ERROR2";
    private static final String RV_STATE_KEY = "RV_STATE_KEY";

    @BindView(R.id.recyclerview_movies)
    RecyclerView mRecyclerView;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;

    private MovieAdapter mMovieAdapter;

    private SharedPreferences sharedPreferences;

    private RecyclerView.LayoutManager layoutManager;

    private Parcelable rvState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
        } else {
            layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        setupSharedPreferences(sharedPreferences);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        rvState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(RV_STATE_KEY, rvState);
        super.onSaveInstanceState(outState);
    }

    protected void onRestoreInstanceState(Bundle state) {
        if (state != null)
            rvState = state.getParcelable(RV_STATE_KEY);
        super.onRestoreInstanceState(state);
    }

    private void setupSharedPreferences(SharedPreferences sharedPreferences) {
        loadSortFromPreferences(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        String value = sharedPreferences.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular_value));

        if (value.equals(getString(R.string.pref_sort_favourites_value))) {
            mMovieAdapter.setMovieList(null);
            loadMovieData(value);
            mMovieAdapter.notifyDataSetChanged();
        }

    }

    private void loadSortFromPreferences(SharedPreferences sharedPreferences) {
        String value = sharedPreferences.getString(getString(R.string.pref_sort_key),
                getString(R.string.pref_sort_popular_value));

        loadMovieData(value);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void loadMovieData(String preference) {
        showMovieListDataView();
        Bundle queryBundle = new Bundle();
        queryBundle.putString(PREFERENCE_STRING_EXTRA, preference);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieListLoader = loaderManager.getLoader(MOVIE_LIST_LOADER);
        if (movieListLoader == null) {
            loaderManager.initLoader(MOVIE_LIST_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_LIST_LOADER, queryBundle, this);
        }
    }


    @Override
    public void onClick(MovieResult movie) {
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, movie);
        startActivity(intentToStartDetailActivity);
    }


    private void showErrorMessage(String error) {
        if (error.equals(ERROR2)) {
            mErrorMessageDisplay.setText(getString(R.string.favourite_empty));
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        } else {
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        }
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void showMovieListDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    @Override
    public Loader<ArrayList<MovieResult>> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<ArrayList<MovieResult>>(this) {

            ArrayList<MovieResult> movieResults = null;

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }

                if (movieResults != null) {
                    deliverResult(movieResults);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public ArrayList<MovieResult> loadInBackground() {
                String preferenceString = args.getString(PREFERENCE_STRING_EXTRA);

                if (preferenceString == null || TextUtils.isEmpty(preferenceString)) {
                    return null;
                }

                if (preferenceString.equals(NetworkUtils.FAVOURITE_MOVIES)) {
                    try {
                        Cursor favCursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                MovieContract.MovieEntry.COLUMN_TIMESTAMP);

                        return getMoviesFromCursor(favCursor);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                URL movieRequestUrl = NetworkUtils.buildMoviesUrl(preferenceString);

                try {
                    String jsonMovieResponse = NetworkUtils
                            .getResponseFromHttpUrl(movieRequestUrl);

                    Results resultsData = MovieJsonUtils
                            .getResultListObjectFromJson(jsonMovieResponse);

                    return (ArrayList<MovieResult>) resultsData.getResults();

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(ArrayList<MovieResult> data) {
                movieResults = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished
            (Loader<ArrayList<MovieResult>> loader, ArrayList<MovieResult> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mMovieAdapter.setMovieList(data);

        if (rvState != null) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(rvState);
        }

        if (null == data) {
            showErrorMessage(ERROR1);
        } else if (data.size() < 1) {
            showErrorMessage(ERROR2);
        } else {
            showMovieListDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieResult>> loader) {

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_key))) {
            loadSortFromPreferences(sharedPreferences);
        }
    }


    public ArrayList<MovieResult> getMoviesFromCursor(Cursor cursor) {

        ArrayList<MovieResult> movieResults = new ArrayList<>();

        int titleIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
        int posterIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH);
        int averageIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_AVERAGE);
        int overviewIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW);
        int releaseIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE);
        int idIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_API_ID);

        MovieResult movie;

        while (cursor.moveToNext()) {
            movie = new MovieResult(cursor.getString(posterIndex),
                    cursor.getString(overviewIndex),
                    cursor.getString(releaseIndex),
                    cursor.getInt(idIndex),
                    cursor.getString(titleIndex),
                    cursor.getDouble(averageIndex));
            movieResults.add(movie);
        }
        return movieResults;
    }


}

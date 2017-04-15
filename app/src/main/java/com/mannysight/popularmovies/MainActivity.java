package com.mannysight.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mannysight.popularmovies.apimodel.MovieList;
import com.mannysight.popularmovies.apimodel.Result;
import com.mannysight.popularmovies.data.MoviePreferences;
import com.mannysight.popularmovies.utilities.MovieJsonUtils;
import com.mannysight.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        loadMovieData(MoviePreferences.POPULAR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.movies_menu, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            new AlertDialog.Builder(this)
                    .setMessage("SORT BY:")
                    .setPositiveButton("Popular", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setListToPopular();
                        }
                    }).setNegativeButton("Top-Rated", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    setListToTopRated();
                }
            }).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setListToTopRated() {
        mMovieAdapter.setMovieList(null);
        loadMovieData(MoviePreferences.TOP_RATED);
    }

    private void setListToPopular() {
        mMovieAdapter.setMovieList(null);
        loadMovieData(MoviePreferences.POPULAR);
    }

    private void loadMovieData(MoviePreferences preferences) {
        showMovieListDataView();
        new FetchMovieTask().execute(preferences);
    }


    @Override
    public void onClick(Result movie) {
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, MovieJsonUtils.serializeMovieToJson(movie));
        startActivity(intentToStartDetailActivity);
    }

    public class FetchMovieTask extends AsyncTask<MoviePreferences, Void, ArrayList<Result>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);

        }

        @Override
        protected ArrayList<Result> doInBackground(MoviePreferences... moviePreferences) {
            if (moviePreferences.length == 0) {
                return null;
            }
            MoviePreferences preference = moviePreferences[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(preference);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                MovieList movieListData = MovieJsonUtils
                        .getMovieListObjectFromJson(jsonMovieResponse);

                return (ArrayList<Result>) movieListData.getResults();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Result> movies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movies != null) {
                showMovieListDataView();
                mMovieAdapter.setMovieList(movies);
            } else {
                showErrorMessage();
            }
        }
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showMovieListDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


}

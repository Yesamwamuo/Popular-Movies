package com.mannysight.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String MOVIES_ARRAY_LIST = "MOVIES ARRAY LIST";

    @BindView(R.id.recyclerview_movies)
    RecyclerView mRecyclerView;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;

    private MovieAdapter mMovieAdapter;
    private ArrayList<Result> mMoviesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMoviesList = new ArrayList<>();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        if (savedInstanceState != null) {
            MovieList movieList = savedInstanceState.getParcelable(MOVIES_ARRAY_LIST);
            List<Result> savedMovieList = null;
            if (movieList != null) {
                savedMovieList = movieList.getResults();
            }
            if (savedMovieList != null && savedMovieList.size() > 0) {
                mMoviesList = (ArrayList<Result>) savedMovieList;
                mMovieAdapter.setMovieList(null);
                mMovieAdapter.setMovieList(mMoviesList);
                mMovieAdapter.notifyDataSetChanged();
            }
        } else {
            loadMovieData(MoviePreferences.POPULAR);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMoviesList.size() > 0) {
            MovieList list = new MovieList();
            list.setResults(mMoviesList);
            outState.putParcelable(MOVIES_ARRAY_LIST, list);
            super.onSaveInstanceState(outState);
        }
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

        if (id == R.id.action_popular) {
            setListToPopular();
            return true;
        }
        if (id == R.id.action_top_rated) {
            setListToTopRated();
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
        new FetchMovieTask(new FetchMovieTaskActionListener()).execute(preferences);
    }


    @Override
    public void onClick(Result movie) {
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, movie);
        startActivity(intentToStartDetailActivity);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showMovieListDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private class FetchMovieTaskActionListener implements AsyncTaskActionListener<ArrayList<Result>> {

        @Override
        public void onTaskBegin() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        public void onTaskComplete(ArrayList<Result> movies) {
            mMoviesList = movies;
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movies != null) {
                showMovieListDataView();
                mMovieAdapter.setMovieList(movies);
            } else {
                showErrorMessage();
            }
        }
    }

}

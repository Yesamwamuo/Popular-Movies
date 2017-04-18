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

import com.mannysight.popularmovies.apimodel.Result;
import com.mannysight.popularmovies.utilities.NetworkUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    @BindView(R.id.recyclerview_movies)
    RecyclerView mRecyclerView;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;

    private MovieAdapter mMovieAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        loadMovieData(NetworkUtils.POPULAR_MOVIES);
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
        loadMovieData(NetworkUtils.TOP_RATED_MOVIES);
    }

    private void setListToPopular() {
        mMovieAdapter.setMovieList(null);
        loadMovieData(NetworkUtils.POPULAR_MOVIES);
    }

    private void loadMovieData(String preference) {
        showMovieListDataView();
        new FetchMovieTask(new FetchMovieTaskActionListener()).execute(preference);
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

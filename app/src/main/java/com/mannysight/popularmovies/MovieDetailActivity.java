package com.mannysight.popularmovies;

import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.mannysight.popularmovies.apimodel.Result;
import com.mannysight.popularmovies.utilities.MovieJsonUtils;
import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    private Result movie;

    private TextView movieTitleTextView;
    private TextView releaseDateTextView;
    private TextView userRatingTextView;
    private TextView overViewTextView;

    private ImageView moviePosterImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                movie = intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);
            }
        }
        movieTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        releaseDateTextView = (TextView) findViewById(R.id.tv_release_date);
        userRatingTextView = (TextView) findViewById(R.id.tv_user_rating);
        overViewTextView = (TextView) findViewById(R.id.tv_overview);

        moviePosterImageView = (ImageView) findViewById(R.id.iv_movie_poster);

        if (movie != null) {
            bind(movie);
        }
    }

    private void bind(Result movie) {
        movieTitleTextView.setText(movie.getTitle());
        releaseDateTextView.setText(movie.getReleaseDate());

        String userRating = "" + movie.getVoteAverage() + "/10";

        userRatingTextView.setText(userRating);
        overViewTextView.setText(movie.getOverview());

        final String IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/w185/";

        String imagePath = IMAGE_BASE_PATH + movie.getPosterPath();
        Picasso.with(this)
                .load(imagePath)
                .fit()
                .placeholder(R.drawable.placeholder_movie)
                .into(moviePosterImageView);
    }
}

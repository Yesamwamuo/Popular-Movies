package com.mannysight.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.mannysight.popularmovies.apimodel.Result;
import com.mannysight.popularmovies.utilities.MoviesDateUtils;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {

    private Result movie;

    @BindView(R.id.tv_movie_title)
    TextView movieTitleTextView;

    @BindView(R.id.tv_release_date)
    TextView releaseDateTextView;

    @BindView(R.id.tv_user_rating)
    TextView userRatingTextView;

    @BindView(R.id.tv_overview)
    TextView overViewTextView;

    @BindView(R.id.iv_movie_poster)
    ImageView moviePosterImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                movie = intentThatStartedThisActivity.getParcelableExtra(Intent.EXTRA_TEXT);
            }
        }

        if (movie != null) {
            bind(movie);
        }
    }

    private void bind(Result movie) {
        movieTitleTextView.setText(movie.getTitle());

        String releaseDateString = movie.getReleaseDate();
        long dateInLong = MoviesDateUtils.getDateLongFromString(releaseDateString);
        String date = MoviesDateUtils.getFriendlyDateString(this, dateInLong);

        releaseDateTextView.setText(date);

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

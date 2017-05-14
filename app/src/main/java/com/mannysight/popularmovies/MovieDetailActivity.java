package com.mannysight.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mannysight.popularmovies.apimodel.MovieResult;
import com.mannysight.popularmovies.apimodel.ResultsForReview;
import com.mannysight.popularmovies.apimodel.ResultsForVideo;
import com.mannysight.popularmovies.apimodel.ReviewResult;
import com.mannysight.popularmovies.apimodel.VideoResult;
import com.mannysight.popularmovies.data.MovieContract;
import com.mannysight.popularmovies.utilities.MovieJsonUtils;
import com.mannysight.popularmovies.utilities.MoviesDateUtils;
import com.mannysight.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler, LoaderManager.LoaderCallbacks<MovieResult> {

    private static final String MOVIE_ID_STRING_EXTRA = "MOVIE_ID_STRING_EXTRA";
    private MovieResult movie;
    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;

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

    @BindView(R.id.movie_detail_pb_loading_indicator)
    ProgressBar movieDetailLoadingIndicator;

    @BindView(R.id.recyclerview_trailers)
    RecyclerView mTrailerRecyclerView;

    @BindView(R.id.recyclerview_reviews)
    RecyclerView mReviewRecyclerView;

    @BindView(R.id.favbutton)
    ImageButton mFavButton;

    private static final int MOVIE_DETAILS_LOADER = 12;

    private boolean sFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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

        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mReviewRecyclerView.setHasFixedSize(true);
        mTrailerRecyclerView.setHasFixedSize(true);

        mReviewAdapter = new ReviewAdapter();
        mTrailerAdapter = new TrailerAdapter(this);

        mReviewRecyclerView.setAdapter(mReviewAdapter);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);

        assert movie != null;
        loadMovieDetails("" + movie.getId());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void bind(MovieResult movie) {
        movieTitleTextView.setText(movie.getTitle());

        String releaseDateString = movie.getReleaseDate();
        long dateInLong = MoviesDateUtils.getDateLongFromString(releaseDateString);
        String date = MoviesDateUtils.getFriendlyDateString(this, dateInLong);

        releaseDateTextView.setText(date);

        String userRating = "" + movie.getVoteAverage() + "/10";

        userRatingTextView.setText(userRating);
        overViewTextView.setText(movie.getOverview());

        sFavourite = isFavourite(movie);

        final String IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/w185/";

        String imagePath = IMAGE_BASE_PATH + movie.getPosterPath();
        Picasso.with(this)
                .load(imagePath)
                .fit()
                .placeholder(R.drawable.placeholder_movie)
                .into(moviePosterImageView);
    }

    private void loadMovieDetails(String movieId) {
        Bundle queryBundle = new Bundle();
        queryBundle.putString(MOVIE_ID_STRING_EXTRA, movieId);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieListLoader = loaderManager.getLoader(MOVIE_DETAILS_LOADER);
        if (movieListLoader == null) {
            loaderManager.initLoader(MOVIE_DETAILS_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_DETAILS_LOADER, queryBundle, this);
        }
    }

    @Override
    public Loader<MovieResult> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<MovieResult>(this) {

            MovieResult movieResult = null;

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }

                if (movieResult != null) {
                    deliverResult(movieResult);
                } else {
                    movieDetailLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public MovieResult loadInBackground() {
                String idString = args.getString(MOVIE_ID_STRING_EXTRA);

                if (idString == null || TextUtils.isEmpty(idString)) {
                    return null;
                }

                URL reviewRequestUrl = NetworkUtils.buildMovieDetailUrl(NetworkUtils.REVIEW_PATH, movie.getId());
                URL videoRequestUrl = NetworkUtils.buildMovieDetailUrl(NetworkUtils.VIDEO_PATH, movie.getId());

                try {
                    String jsonReviewResponse = NetworkUtils
                            .getResponseFromHttpUrl(reviewRequestUrl);
                    String jsonVideoResponse = NetworkUtils
                            .getResponseFromHttpUrl(videoRequestUrl);

                    ResultsForReview resultForReviewObjectFromJson = MovieJsonUtils
                            .getResultForReviewObjectFromJson(jsonReviewResponse);
                    ResultsForVideo resultForVideoObjectFromJson = MovieJsonUtils
                            .getResultForVideoObjectFromJson(jsonVideoResponse);

                    movie.setReviewResults(resultForReviewObjectFromJson.getResults());
                    movie.setVideoResults(resultForVideoObjectFromJson.getResults());

                    return movie;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(MovieResult data) {
                movieResult = data;
                super.deliverResult(data);
            }
        };
    }


    @Override
    public void onLoadFinished
            (Loader<MovieResult> loader, MovieResult data) {

        movieDetailLoadingIndicator.setVisibility(View.GONE);

        List<ReviewResult> reviewResults = new ArrayList<>();
        List<VideoResult> videoResults = new ArrayList<>();


        reviewResults = data.getReviewResults();
        mReviewAdapter.setReviewList((ArrayList<ReviewResult>) reviewResults);


        videoResults = data.getVideoResults();
        mTrailerAdapter.setVideoList((ArrayList<VideoResult>) videoResults);

    }

    @Override
    public void onLoaderReset(Loader<MovieResult> loader) {

    }



    public void toggleFavourites(View view) {
        if (!sFavourite) {
            addToFavorites(movie);
        } else {
            removeFromFavourites(movie);
        }
    }

    private void toggleFavBtnTint(Boolean sFavourite) {
        if (!sFavourite) {
            mFavButton.setColorFilter(ContextCompat.getColor(this, R.color.favColoDefault));
        } else {
            mFavButton.setColorFilter(ContextCompat.getColor(this, R.color.favColoTint));
        }
    }


    private void removeFromFavourites(MovieResult movie) {
        int id = (int) movie.getId();

        String stringId = Integer.toString(id);
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        int deletedRows = getContentResolver().delete(uri, null, null);

        if (deletedRows > 0) {
            sFavourite = false;
            toggleFavBtnTint(sFavourite);
        }
    }

    private void addToFavorites(MovieResult movie) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, movie.getPosterPath());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_AVERAGE, movie.getVoteAverage());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_API_ID, movie.getId());

        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

        if (uri != null) {
            sFavourite = true;
            toggleFavBtnTint(sFavourite);
        }
    }

    private boolean isFavourite(MovieResult movie) {

        int id = (int) movie.getId();

        String stringId = Integer.toString(id);
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();

        Cursor query = getContentResolver().query(uri, null, null, null, null);

        Boolean value = false;

        if (query != null) {
            value = query.getCount() > 0;
        }
        toggleFavBtnTint(value);
        return value;
    }


    @Override
    public void onClickTrailer(String trailerId) {
        watchYoutubeVideo(trailerId);
    }

    public void watchYoutubeVideo(String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }
}

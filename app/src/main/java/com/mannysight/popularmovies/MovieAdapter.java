package com.mannysight.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mannysight.popularmovies.apimodel.MovieResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wamuo on 4/15/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private ArrayList<MovieResult> mMovieList;
    private Context context;

    private final MovieAdapterOnClickHandler mClickHandler;

    public MovieAdapter(MovieAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }


    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        MovieResult movie = mMovieList.get(position);
        holder.bind(context, movie);
    }

    @Override
    public int getItemCount() {
        if (mMovieList == null) return 0;
        return mMovieList.size();
    }

    public void setMovieList(ArrayList<MovieResult> movieList) {
        mMovieList = movieList;
        notifyDataSetChanged();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_movie_poster)
        ImageView moviePosterImageView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }


        private void bind(Context context, MovieResult movie) {

            final String IMAGE_BASE_PATH = "http://image.tmdb.org/t/p/w185/";

            String imagePath = IMAGE_BASE_PATH + movie.getPosterPath();
            Picasso.with(context)
                    .load(imagePath)
                    .fit()
                    .placeholder(R.drawable.placeholder_movie)
                    .into(moviePosterImageView);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            MovieResult movie = mMovieList.get(adapterPosition);
            mClickHandler.onClick(movie);
        }
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(MovieResult movie);
    }
}

package com.mannysight.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mannysight.popularmovies.apimodel.ReviewResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wamuo on 5/8/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private ArrayList<ReviewResult> reviewList;
    private Context context;

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new ReviewAdapter.ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        ReviewResult review = reviewList.get(position);
        holder.bind(context, review);
    }

    @Override
    public int getItemCount() {
        if (reviewList == null) return 0;
        return reviewList.size();
    }

    public void setReviewList(ArrayList<ReviewResult> reviewResults) {

        reviewList = (ArrayList<ReviewResult>) trimmedReviews(reviewResults);
        notifyDataSetChanged();
    }

    private List<ReviewResult> trimmedReviews(List<ReviewResult> results) {
        int trimmedLength = 3;
        ArrayList<ReviewResult> trimmedList = new ArrayList<>();
        for (int i = 0; i < trimmedLength && i < results.size(); i++) {
            trimmedList.add(results.get(i));
        }
        return trimmedList;
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.review_author)
        TextView reviewAuthor;

        @BindView(R.id.review_content)
        TextView reviewContent;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Context context, ReviewResult review) {
            reviewAuthor.setText(review.getAuthor());
            reviewContent.setText(review.getContent());
        }
    }
}

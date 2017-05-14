package com.mannysight.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mannysight.popularmovies.apimodel.VideoResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wamuo on 5/8/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private ArrayList<VideoResult> videoList;
    private Context context;

    private final TrailerAdapterOnClickHandler mClickHandler;

    public TrailerAdapter(TrailerAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new TrailerAdapter.TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        holder.bind(context);
    }

    @Override
    public int getItemCount() {
        if (videoList == null) return 0;
        return videoList.size();
    }

    public void setVideoList(ArrayList<VideoResult> videoResults) {
        videoList = (ArrayList<VideoResult>) trimmedVideos(videoResults);
        notifyDataSetChanged();
    }

    private List<VideoResult> trimmedVideos(List<VideoResult> results) {
        int trimmedLength = 3;
        ArrayList<VideoResult> trimmedList = new ArrayList<>();
        for (int i = 0; i < trimmedLength && i < results.size(); i++) {
            trimmedList.add(results.get(i));
        }
        return trimmedList;
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.trailer_text)
        TextView trailerText;

        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(Context context) {
            int number = getAdapterPosition() + 1;
            String text = context.getString(R.string.trailerText, number);
            trailerText.setText(text);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            VideoResult videoResult = videoList.get(adapterPosition);
            String trailerId = videoResult.getKey();
            mClickHandler.onClickTrailer(trailerId);
        }
    }

    public interface TrailerAdapterOnClickHandler {
        void onClickTrailer(String trailerId);
    }
}
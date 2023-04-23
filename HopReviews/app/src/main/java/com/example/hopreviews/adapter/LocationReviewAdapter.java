package com.example.hopreviews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hopreviews.R;
import com.example.hopreviews.data.model.LocationReview;

import java.util.List;

public class LocationReviewAdapter extends RecyclerView.Adapter<LocationReviewAdapter.LocationReviewViewHolder> {

    private final Context context;
    private final List<LocationReview> dataset;
    private final ClickListener clickListener;


    public LocationReviewAdapter(Context context, List<LocationReview> dataset, ClickListener clickListener) {
        this.context = context;
        this.dataset = dataset;
        this.clickListener = clickListener;
    }
    public class LocationReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView userName;
        public RatingBar userRating;
        public TextView userLocationReview;
        public TextView LocationReviewDate;
        public Button upvote;
        public Button downvote;

        public LocationReviewViewHolder(View view) {
            super(view);
            userName = view.findViewById(R.id.user_name_location);
            userRating = view.findViewById(R.id.user_rating_location);
            userLocationReview = view.findViewById(R.id.review_text_location);
            LocationReviewDate = view.findViewById(R.id.review_date_location);
            upvote = view.findViewById(R.id.upvote_button);
            downvote = view.findViewById(R.id.downvote_button);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //passing the clicked position to the parent class
            int position = getAdapterPosition();
            System.out.println(position);
            if (position >= 0) {
                clickListener.onItemClick(position, view);
            }
        }
    }

    @Override
    public LocationReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View adapterLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_item, parent, false);
        return new LocationReviewViewHolder(adapterLayout);
    }
    @Override
    public void onBindViewHolder(LocationReviewViewHolder holder, int position) {
        LocationReview item = dataset.get(position);
        holder.LocationReviewDate.setText(item.getDate());
        holder.userLocationReview.setText(item.getReview());
        holder.userName.setText(item.getUser());
        holder.userRating.setRating(item.getRating());
        holder.userRating.setNumStars(5);
        holder.upvote.setOnClickListener(v -> {

        });
        holder.downvote.setOnClickListener(v -> {

        });
    }
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public LocationReview getItem(int position) {
        return dataset.get(position);
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}

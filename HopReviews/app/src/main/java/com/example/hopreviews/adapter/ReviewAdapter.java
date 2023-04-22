package com.example.hopreviews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hopreviews.R;
import com.example.hopreviews.data.model.Review;

import java.util.List;

// a lot of this code is derived from its Kotlin version on an open source
// android tutorial https://developer.android.com/codelabs/basic-android-kotlin-training-display-list-cards#3

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final Context context;
    private final List<Review> dataset;
    private static AdapterView.OnItemClickListener onItemClickListener;


    public ReviewAdapter(Context context, List<Review> dataset, AdapterView.OnItemClickListener onItemClickListener) {
        this.context = context;
        this.dataset = dataset;
        this.onItemClickListener = onItemClickListener;
    }
    public static class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView location;
        public TextView userName;
        public RatingBar userRating;
        public TextView userReview;
        public TextView reviewDate;

        public ReviewViewHolder(View view) {
            super(view);
            location = view.findViewById(R.id.review_location);
            userName = view.findViewById(R.id.user_name);
            userRating = view.findViewById(R.id.user_rating);
            userReview = view.findViewById(R.id.review_text);
            reviewDate = view.findViewById(R.id.review_date);
        }

        @Override
        public void onClick(View view) {
            //passing the clicked position to the parent class
            onItemClickListener.onItemClick(null, view, getAdapterPosition(), view.getId());
        }
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View adapterLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(adapterLayout);
    }
    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Review item = dataset.get(position);
        holder.reviewDate.setText(item.getDate());
        holder.location.setText(item.getLocation());
        holder.userReview.setText(item.getReview());
        holder.userName.setText(item.getUser());
        holder.userRating.setRating(item.getRating());
        holder.userRating.setNumStars(5);
    }
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public Review getItem(int position) {
        return dataset.get(position);
    }
}

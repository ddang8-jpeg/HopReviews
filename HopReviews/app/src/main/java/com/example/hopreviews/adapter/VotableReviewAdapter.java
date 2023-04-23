package com.example.hopreviews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hopreviews.R;
import com.example.hopreviews.data.model.VotableReview;

import java.util.List;

public class VotableReviewAdapter extends RecyclerView.Adapter<VotableReviewAdapter.VotableReviewViewHolder> {

    private final Context context;
    private final List<VotableReview> dataset;
    private final ClickListener clickListener;


    public VotableReviewAdapter(Context context, List<VotableReview> dataset, ClickListener clickListener) {
        this.context = context;
        this.dataset = dataset;
        this.clickListener = clickListener;
    }
    public class VotableReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView location;
        public TextView userName;
        public RatingBar userRating;
        public TextView userVotableReview;
        public TextView VotableReviewDate;

        public VotableReviewViewHolder(View view) {
            super(view);
            location = view.findViewById(R.id.location_votable);
            userName = view.findViewById(R.id.user_name_votable);
            userRating = view.findViewById(R.id.user_rating_votable);
            userVotableReview = view.findViewById(R.id.review_text_votable);
            VotableReviewDate = view.findViewById(R.id.review_date_votable);
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
    public VotableReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View adapterLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.votable_review_item, parent, false);
        return new VotableReviewViewHolder(adapterLayout);
    }
    @Override
    public void onBindViewHolder(VotableReviewViewHolder holder, int position) {
        VotableReview item = dataset.get(position);
        holder.VotableReviewDate.setText(item.getDate());
        holder.location.setText(item.getLocation());
        holder.userVotableReview.setText(item.getReview());
        holder.userName.setText(item.getUser());
        holder.userRating.setRating(item.getRating());
        holder.userRating.setNumStars(5);
    }
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public VotableReview getItem(int position) {
        return dataset.get(position);
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
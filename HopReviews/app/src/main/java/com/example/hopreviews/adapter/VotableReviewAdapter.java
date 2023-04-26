package com.example.hopreviews.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hopreviews.R;
import com.example.hopreviews.data.model.VotableReview;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        public Button upvote;
        public Button downvote;

        public VotableReviewViewHolder(View view) {
            super(view);
            location = view.findViewById(R.id.location_votable);
            userName = view.findViewById(R.id.user_name_votable);
            userRating = view.findViewById(R.id.user_rating_votable);
            userVotableReview = view.findViewById(R.id.review_text_votable);
            VotableReviewDate = view.findViewById(R.id.review_date_votable);
            upvote = view.findViewById(R.id.upvote_button);
            downvote = view.findViewById(R.id.downvote_button);
            upvote.setOnClickListener(this);
            downvote.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //passing the clicked position to the parent class
            int position = getAdapterPosition();
            if (position >= 0) {
                if (view.getId() == R.id.upvote_button) {
                    clickListener.onUpVote(position, view);
                } else if (view.getId() == R.id.downvote_button) {
                    clickListener.onDownVote(position, view);
                } else {
                    clickListener.onItemClick(position, view);
                }
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
        holder.upvote.setText(String.valueOf(item.getUpvotes().size()));
        holder.downvote.setText(String.valueOf(item.getDownvotes().size()));
        SharedPreferences sp = context.getSharedPreferences("email", Context.MODE_PRIVATE);
        String requester = sp.getString("email", "");
        if (item.getUpvotes().contains(encodeEmail(requester))) {
            holder.upvote.setBackgroundColor(Color.parseColor("#3C5E15"));
        }
        if (item.getDownvotes().contains(encodeEmail(requester))) {
            holder.downvote.setBackgroundColor(Color.parseColor("#BA3636"));
        }
    }
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public VotableReview getItem(int position) {
        return dataset.get(position % getItemCount());
    }

    private String encodeEmail(String str) {
        str = str.replaceAll("@", "-");
        str = str.replaceAll("\\.", "_");
        return str;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onUpVote(int position, View v);
        void onDownVote(int position, View v);
    }
}

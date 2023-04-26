package com.example.hopreviews.ui.feed;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hopreviews.LocationActivity;
import com.example.hopreviews.R;
import com.example.hopreviews.adapter.ReviewAdapter;
import com.example.hopreviews.adapter.VotableReviewAdapter;
import com.example.hopreviews.data.model.Review;
import com.example.hopreviews.data.model.VotableReview;
import com.example.hopreviews.databinding.FragmentFeedBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;
    private RecyclerView listView;
    ArrayList<VotableReview> reviews;
    DatabaseReference ref;
    VotableReviewAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        FeedViewModel feedViewModel =
                new ViewModelProvider(this).get(FeedViewModel.class);

        binding = FragmentFeedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setHasOptionsMenu(true);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("feed");
        listView = binding.feedList;
        reviews = new ArrayList<>();
        adapter = new VotableReviewAdapter(getContext(), reviews, new VotableReviewAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                VotableReview item = adapter.getItem(position);
                String location = item.getLocation();
                Intent intent = new Intent(getActivity(), LocationActivity.class);
                intent.putExtra("name", location);
                startActivity(intent);
            }

            @Override
            public void onUpVote(int position, View v) {
                VotableReview item = adapter.getItem(position);
                String location = item.getLocation();
                String timestamp = item.getTimestamp();
                String user = item.getUser();
                ArrayList<String> upvotes = item.getUpvotes();
                ArrayList<String> downvotes = item.getDownvotes();
                String voter = encodeEmail(getActivity()
                                .getSharedPreferences("email",
                                        Context.MODE_PRIVATE).getString("email", ""));
                Button up = v.findViewById(R.id.upvote_button);
                Button down = null;
                if (upvotes.contains(voter)) {
                    ref.child(timestamp).child(location).child(encodeEmail(user)).child("likes").child(voter).setValue(false);
                    upvotes.remove(voter);
                    up.setBackgroundColor(Color.parseColor("#8AB557"));
                } else {
                    ref.child(timestamp).child(location).child(encodeEmail(user)).child("likes").child(voter).setValue(true);
                    upvotes.add(voter);
                    up.setBackgroundColor(Color.parseColor("#3C5E15"));
                }
                ViewGroup card = (ViewGroup) v.getParent();
                for (int itemPos = 0; itemPos < card.getChildCount(); itemPos++) {
                    View view = card.getChildAt(itemPos);
                    if (view.getId() == R.id.downvote_button) {
                        down = (Button) view;
                        break;
                    }
                }
                downvotes.remove(voter);
                down.setBackgroundColor(Color.parseColor("#CF8080"));
                ref.child(timestamp).child(location).child(encodeEmail(user)).child("dislikes").child(voter).setValue(false);
                up.setText(String.valueOf(upvotes.size()));
                down.setText(String.valueOf(downvotes.size()));
            }

            @Override
            public void onDownVote(int position, View v) {
                VotableReview item = adapter.getItem(position);
                String location = item.getLocation();
                String timestamp = item.getTimestamp();
                String user = item.getUser();
                ArrayList<String> upvotes = item.getUpvotes();
                ArrayList<String> downvotes = item.getDownvotes();
                String voter = encodeEmail(getActivity()
                        .getSharedPreferences("email",
                                Context.MODE_PRIVATE).getString("email", ""));
                Button up = null;
                Button down = v.findViewById(R.id.downvote_button);
                ViewGroup card = (ViewGroup) v.getParent();
                for (int itemPos = 0; itemPos < card.getChildCount(); itemPos++) {
                    View view = card.getChildAt(itemPos);
                    if (view.getId() == R.id.upvote_button) {
                        up = (Button) view;
                        break;
                    }
                }
                up.setBackgroundColor(Color.parseColor("#8AB557"));
                upvotes.remove(voter);
                if (downvotes.contains(voter)) {
                    ref.child(timestamp).child(location).child(encodeEmail(user)).child("dislikes").child(voter).setValue(false);
                    downvotes.remove(voter);
                    down.setBackgroundColor(Color.parseColor("#CF8080"));
                } else {
                    ref.child(timestamp).child(location).child(encodeEmail(user)).child("dislikes").child(voter).setValue(true);
                    downvotes.add(voter);
                    down.setBackgroundColor(Color.parseColor("#BA3636"));
                }
                ref.child(timestamp).child(location).child(encodeEmail(user)).child("likes").child(voter).setValue(false);
                up.setText(String.valueOf(upvotes.size()));
                down.setText(String.valueOf(downvotes.size()));
            }
        });

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Iterable<DataSnapshot> timestamps = snapshot.getChildren();

                for (DataSnapshot timestamp: timestamps) {
                    Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) timestamp.getValue();
                    if (map != null) {
                        for (String user: map.keySet()) {
                            ArrayList<String> likedBy = new ArrayList<>();
                            ArrayList<String> dislikedBy = new ArrayList<>();
                            if (timestamp.child(encodeEmail(user)).child("likes").getValue() != null) {
                                Map<String, Boolean> likes = (Map<String, Boolean>) timestamp
                                        .child(encodeEmail(user)).child("likes").getValue();
                                for (String like : likes.keySet()) {
                                    if (likes.get(like)) {
                                        likedBy.add(like);
                                    }
                                }
                            }
                            if (timestamp.child(encodeEmail(user)).child("dislikes").getValue() != null) {
                                Map<String, Boolean> dislikes = (Map<String, Boolean>) timestamp
                                        .child(encodeEmail(user)).child("dislikes").getValue();
                                for (String dislike : dislikes.keySet()) {
                                    if (dislikes.get(dislike)) {
                                        dislikedBy.add(dislike);
                                    }
                                }
                            }
                            VotableReview item = createListItem(user,
                                    timestamp.getRef().getParent().getKey(), map.get(user).get("review"),
                                    map.get(user).get("rating"), timestamp.getKey(), likedBy, dislikedBy);
                            reviews.add(0, item);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        listView.setAdapter(adapter);
        return root;
    }

    private VotableReview createListItem(String username, String timestamp, String review,
                                         String rating, String location, ArrayList<String> likes, ArrayList<String> dislikes) {
        Date date = new Date(Long.parseLong(timestamp));
        if (rating == null) {
            rating = "3.0";
        }
        VotableReview reviewItem = new VotableReview(review, date.toLocaleString(),
                location, decodeEmail(username), Float.parseFloat(rating), timestamp, likes, dislikes);
        return reviewItem;
    }

    private String encodeEmail(String str) {
        str = str.replaceAll("@", "-");
        str = str.replaceAll("\\.", "_");
        return str;
    }

    private String decodeEmail(String str) {
        if (str == null || str.isEmpty()) {
            return "Anonymous User";
        }
        str = str.replaceAll("-", "@");
        str = str.replaceAll("_", ".");
        return str;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.getItem(1).setVisible(false);
    }
}
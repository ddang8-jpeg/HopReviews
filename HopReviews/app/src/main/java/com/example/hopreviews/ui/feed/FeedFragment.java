package com.example.hopreviews.ui.feed;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hopreviews.LocationActivity;
import com.example.hopreviews.adapter.ReviewAdapter;
import com.example.hopreviews.data.model.Review;
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
    ArrayList<Review> reviews;
    DatabaseReference ref;
    ReviewAdapter adapter;

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
        adapter = new ReviewAdapter(getContext(), reviews, (position, v) -> {
            Review item = adapter.getItem(position);
            String location = item.getLocation();
            Intent intent = new Intent(getActivity(), LocationActivity.class);
            intent.putExtra("name", location);
            startActivity(intent);
        });
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Iterable<DataSnapshot> timestamps = snapshot.getChildren();
                for (DataSnapshot timestamp: timestamps) {
                    Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) timestamp.getValue();
                    if (map != null) {
                        for (String location: map.keySet()) {
                                Review item = createListItem(location, timestamp.getRef().getParent().getKey(), map.get(location).get("review"),
                                        map.get(location).get("rating"), timestamp.getKey());
                                reviews.add(item);
                            }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
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

    private Review createListItem(String username, String timestamp, String review, String rating, String location) {
        Date date = new Date(Long.parseLong(timestamp));
        Review reviewItem = new Review(review, date.toLocaleString(), location, decodeEmail(username), Float.parseFloat(rating));
        return reviewItem;
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
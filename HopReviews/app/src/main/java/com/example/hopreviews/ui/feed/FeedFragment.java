package com.example.hopreviews.ui.feed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.hopreviews.databinding.FragmentFeedBinding;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;
    private ListView listView;
    ArrayList<String> reviews;
    DatabaseReference ref;
    ArrayAdapter<String> adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        FeedViewModel feedViewModel =
                new ViewModelProvider(this).get(FeedViewModel.class);

        binding = FragmentFeedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("feed");
        listView = binding.feedList;
        reviews = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, reviews);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Iterable<DataSnapshot> timestamps = snapshot.getChildren();
                for (DataSnapshot timestamp: timestamps) {
                    Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) timestamp.getValue();
                    if (map != null) {
                        for (String location: map.keySet()) {
                                String item = createListItem(location, timestamp.getRef().getParent().getKey(), map.get(location).get("review"),
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

    private String createListItem(String username, String timestamp, String review, String rating, String location) {
        Date date = new Date(Long.parseLong(timestamp));
        return "\nLocation: " + location + "\n\nUsername: " + decodeEmail(username) + "\nRating: "
                + rating + "/5.0\n\nReview: " + review + "\n\nDate Posted: " + date.toLocaleString() + "\n";
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
}
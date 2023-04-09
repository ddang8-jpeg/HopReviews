package com.example.hopreviews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hopreviews.databinding.ActivityLocationBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class LocationActivity extends AppCompatActivity {

    ActivityLocationBinding binding;
    private ListView listView;
    ArrayList<String> reviews;
    DatabaseReference ref;
    DatabaseReference likesRef;
    ArrayAdapter<String> adapter;
    Set<String> likes = new HashSet<>();
    Set<String> dislikes = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        reviews = new ArrayList<>();

        initializeLikes();
        initializeList();
    }
    private void initializeLikes() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        likesRef = database.getReference("likes").child(getIntent().getStringExtra("name"));
        Button likesBtn = (Button) binding.likesBtn;
        Button dislikesBtn = (Button) binding.dislikesBtn;
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Long> mapLikes = (Map<String, Long>) snapshot.child("likes").getValue();
                Map<String, Long> mapDislikes = (Map<String, Long>) snapshot.child("dislikes").getValue();
                if (mapLikes != null) {
                    for (String user: mapLikes.keySet()) {
                        if (mapLikes.get(user) == 1) {
                            likes.add(user);
                        }
                    }
                }
                if (mapDislikes != null) {
                    for (String user: mapDislikes.keySet()) {
                        if (mapDislikes.get(user) == 1) {
                            dislikes.add(user);
                        }
                    }
                }
                likesBtn.setText("Likes: " + likes.size());
                dislikesBtn.setText("Dislikes: " + dislikes.size());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("loadPost:onCancelled", databaseError.toException());
            }
        };
        String encodedUser = encodeEmail(getIntent().getStringExtra("username"));
        likesBtn.setOnClickListener(view -> {
            if (dislikes.contains(encodedUser)) {
                likesRef.child("dislikes").child(encodedUser).setValue(0);
                dislikes.remove(encodedUser);
            }
            if (likes.contains(encodedUser)) {
                likesRef.child("likes").child(encodedUser).setValue(0);
                likes.remove(encodedUser);
            } else {
                likesRef.child("likes").child(encodedUser).setValue(1);
                likes.add(encodedUser);
            }
        });
        dislikesBtn.setOnClickListener(view -> {
            if (likes.contains(encodedUser)) {
                likesRef.child("likes").child(encodedUser).setValue(0);
                likes.remove(encodedUser);
            }
            if (dislikes.contains(encodedUser)) {
                likesRef.child("dislikes").child(encodedUser).setValue(0);
                dislikes.remove(encodedUser);
            } else {
                likesRef.child("dislikes").child(encodedUser).setValue(1);
                dislikes.add(encodedUser);
            }
        });
        likesRef.addValueEventListener(listener);
    }
    private void initializeList() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("reviews");
        listView = binding.reviewlist;
        adapter = new ArrayAdapter<>(LocationActivity.this, android.R.layout.simple_list_item_1, reviews);
        ref.child(getIntent().getStringExtra("name")).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Map<String, String> map = (Map<String, String>) snapshot.getValue();
                TreeMap<String, String> sorted = null;
                if (map != null) {
                    sorted = new TreeMap<>(map);
                }
                ArrayList<String> sortedKeys = null;
                if (sorted != null) {
                    sortedKeys = new ArrayList<>(sorted.keySet());
                }
                Collections.reverse(sortedKeys);
                if (map != null) {
                    for (String timestamp: sortedKeys) {
                        String item = createListItem(snapshot.getKey(), timestamp, map.get(timestamp));
                        reviews.add(item);
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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AddReviewActivity.class);
            intent.putExtra("name", getIntent().getStringExtra("name"));
            intent.putExtra("username", getIntent().getStringExtra("username"));
            startActivityForResult(intent, 0);
        });
        listView.setAdapter(adapter);
    }

    private String createListItem(String username, String timestamp, String review) {
        Date date = new Date(Long.parseLong(timestamp));
        return "\nUsername: " + decodeEmail(username) + "\n\nReview: " + review +
                "\n\nDate Posted: " + date.toLocaleString() + "\n";
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
       if (resultCode == 1) {
           return;
       } else if (resultCode == 0) {
           String newReview = data.getStringExtra("newlyadded");
           reviews.add(newReview);
           adapter.notifyDataSetChanged();
       }
    }

}
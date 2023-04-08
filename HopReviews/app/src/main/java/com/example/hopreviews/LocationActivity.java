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

import com.example.hopreviews.databinding.ActivityLocationBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class LocationActivity extends AppCompatActivity {

    ActivityLocationBinding binding;
    private ListView listView;
    ArrayList<String> reviews;
    DatabaseReference ref;
    DatabaseReference likesRef;
    ArrayAdapter<String> adapter;
    private Long likes;
    private  Long dislikes;

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
                likes = snapshot.child("likes").getValue(Long.class);
                dislikes = snapshot.child("dislikes").getValue(Long.class);
                if (likes == null) {
                    likesBtn.setText("Likes: 0");
                } else {
                    likesBtn.setText("Likes: " + likes);
                }
                if (dislikes == null) {
                    dislikesBtn.setText("Dislikes: 0");
                } else {
                    dislikesBtn.setText("Dislikes: " + dislikes);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("loadPost:onCancelled", databaseError.toException());
            }
        };

        likesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (likes != null) {
                    likesRef.child("likes").setValue(likes + 1);
                } else {
                    likesRef.child("likes").setValue(1);
                }
            }
        });
        dislikesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dislikes != null) {
                    likesRef.child("dislikes").setValue(dislikes + 1);
                } else {
                    likesRef.child("dislikes").setValue(1);
                }
            }
        });
        likesRef.addValueEventListener(listener);
    }
    private void initializeList() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("reviews");
        listView = (ListView) binding.reviewlist;
        adapter = new ArrayAdapter<>(LocationActivity.this, android.R.layout.simple_list_item_1, reviews);
        ref.child(getIntent().getStringExtra("name")).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Map<String, String> map = (Map<String, String>) snapshot.getValue();
                for (String timestamp: map.keySet()) {
                    reviews.add(map.get((String) timestamp));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                for (Object timestamp: snapshot.getValue(Map.class).entrySet()) {
                    reviews.remove((String) snapshot.getValue(Map.class).get((String) timestamp));
                }
                adapter.notifyDataSetChanged();
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
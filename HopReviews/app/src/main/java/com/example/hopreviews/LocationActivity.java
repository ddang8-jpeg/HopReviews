package com.example.hopreviews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.hopreviews.adapter.LocationReviewAdapter;
import com.example.hopreviews.data.model.LocationReview;
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
    private RecyclerView listView;
    ArrayList<LocationReview> reviews;
    DatabaseReference ref;
    DatabaseReference likesRef;
    LocationReviewAdapter adapter;
    Set<String> likes = new HashSet<>();
    Set<String> dislikes = new HashSet<>();
    private boolean favorite;

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
        favorite = false;
        initializeLikes();
        initializeList();
    }

    private void isFavorite(Menu menu) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference favRef = database.getReference("users");
        String username = getSharedPreferences("email", MODE_PRIVATE).getString("email", "");
        favRef.child(encodeEmail(username)).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        try {
                            Map<String, Boolean> map = (Map<String, Boolean>) snapshot.getValue();
                            if (map.get(getIntent().getStringExtra("name"))) {
                                menu.getItem(0).setIcon(R.drawable.baseline_star_24);
                                favorite = true;
                            }
                        } catch (Exception e) {

                        }
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
    }
    private void initializeLikes() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        likesRef = database.getReference("locations").child(getIntent().getStringExtra("name"));
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
        String username = getSharedPreferences("email", MODE_PRIVATE).getString("email", "");
        String encodedUser = encodeEmail(username);
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
        ref = database.getReference("locations");
        listView = binding.reviewlist;
        adapter = new LocationReviewAdapter(getApplicationContext(), reviews, (position, v) -> {});
        ref.child(getIntent().getStringExtra("name")).child("reviews").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) snapshot.getValue();
                if (map != null) {
                    for (String user: map.keySet()) {
                        if (map.get(user).get("rating") != null) {
                            LocationReview item = createListItem(user, snapshot.getKey(),
                                    map.get(user).get("review"), map.get(user).get("rating"));
                            reviews.add(0, item);
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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AddReviewActivity.class);
            intent.putExtra("name", getIntent().getStringExtra("name"));
            String username = getSharedPreferences("email", MODE_PRIVATE).getString("email", "");
            intent.putExtra("username", username);
            startActivityForResult(intent, 0);
        });
        listView.setAdapter(adapter);
    }

    private LocationReview createListItem(String username, String timestamp, String review, String rating) {
        Date date = new Date(Long.parseLong(timestamp));
        if (rating == null) {
            rating = "3.0";
        }
        LocationReview item = new LocationReview(review, date.toLocaleString(),
                decodeEmail(username), Float.parseFloat(rating));
        return item;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_favorite, menu);
        isFavorite(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String username = getSharedPreferences("email", MODE_PRIVATE).getString("email", "");
        if (item.getItemId() == R.id.add_to_favorite && !favorite) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("users");
            ref.child(encodeEmail(username)).child("favorites")
                    .child(getIntent().getStringExtra("name")).setValue(true);
            Toast.makeText(this, "Successfully added to favorites", Toast.LENGTH_SHORT).show();
            item.setIcon(R.drawable.baseline_star_24);
            favorite = true;
            return true;
        } else if (item.getItemId() == R.id.add_to_favorite && favorite) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("users");
            ref.child(encodeEmail(username)).child("favorites")
                    .child(getIntent().getStringExtra("name")).setValue(false);
            Toast.makeText(this, "Successfully removed from favorites", Toast.LENGTH_SHORT).show();
            item.setIcon(R.drawable.baseline_star_outline_24);
            favorite = false;
            return true;
        } else if (item.getItemId() == R.id.gallery) {
            Intent intent = new Intent(this, GalleryActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("name", getIntent().getStringExtra("name"));
            startActivity(intent);
            return true;
        }
        this.finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
           return;
       } else if (resultCode == 0) {
            String username = data.getStringExtra("username");
            String newTime = data.getStringExtra("timestamp");
            String newReview = data.getStringExtra("newlyadded");
            String newRating = data.getStringExtra("rating");

           LocationReview item = createListItem(username, newTime, newReview, newRating);
           for (LocationReview review: reviews) {
               if (review.getDate().equals(item.getDate())) {
                   return;
               }
           }
           reviews.add(0, item);
           adapter.notifyDataSetChanged();
       }
    }

}
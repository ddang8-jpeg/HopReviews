package com.example.hopreviews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hopreviews.adapter.ReviewAdapter;
import com.example.hopreviews.data.model.Review;
import com.example.hopreviews.databinding.ActivityProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private SharedPreferences sharedPreferences;
    RecyclerView listView;
    ArrayList<Review> reviews;
    ReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        sharedPreferences = getSharedPreferences("email", Context.MODE_PRIVATE);
        TextView firstLastNameText = binding.firstLastName;
        TextView yearText = binding.year;
        TextView emailText = binding.email;
        listView = binding.myReviews;

        reviews = new ArrayList<>();
        adapter = new ReviewAdapter(getApplicationContext(), reviews, (position, v) -> {
            Review item = adapter.getItem(position);
            String location = item.getLocation();
            Intent intent = new Intent(this, LocationActivity.class);
            intent.putExtra("name", location);
            startActivity(intent);
        });

        String userEmail = sharedPreferences.getString("email", "");
        String em = encodeEmail(userEmail);
        DatabaseReference ref = database.getReference("users").child(em);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = (String) snapshot.child("firstName").getValue();
                String lastName = (String) snapshot.child("lastName").getValue();
                String year = (String) snapshot.child("year").getValue();
                String email = (String) snapshot.child("email").getValue();
                String name = firstName + " " + lastName;
                firstLastNameText.setText(name);
                yearText.setText(year);
                emailText.setText(email);
                Map<String, Map<String, String>> map =
                        (Map<String, Map<String, String>>) snapshot.child("reviews").getValue();
                for (String key: map.keySet()) {
                    Review item = createListItem(email, key, map.get(key).get("review"),
                            map.get(key).get("rating"), map.get(key).get("location"));
                    boolean getOut = false;
                    for (Review review: reviews) {
                        if (review.getDate().equals(item.getDate())) {
                            getOut = true;
                            continue;
                        }
                    }
                    if (getOut) {
                        continue;
                    }
                    reviews.add(0, item);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("loadPost:onCancelled", databaseError.toException());
            }
        };
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ref.addValueEventListener(listener);
        listView.setAdapter(adapter);
    }

    private Review createListItem(String username, String timestamp, String review, String rating, String location) {
        Date date = new Date(Long.parseLong(timestamp));
        Review reviewItem = new Review(review, date.toLocaleString(), location, decodeEmail(username), Float.parseFloat(rating));
        return reviewItem;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_profile) {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivityForResult(intent, 100);
            return true;
        } else {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
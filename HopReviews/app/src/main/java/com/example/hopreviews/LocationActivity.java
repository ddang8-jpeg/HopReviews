package com.example.hopreviews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.hopreviews.databinding.ActivityLocationBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class LocationActivity extends AppCompatActivity {

    ActivityLocationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("reviews");
        ref.child(getIntent().getStringExtra("name")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> reviews = collectReviews((Map<String, Object>) snapshot.getValue());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.activity_location, reviews);
                ListView listView = (ListView) findViewById(R.id.reviewlist);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddReviewActivity.class);
                intent.putExtra("name", getIntent().getStringExtra("name"));
                startActivity(intent);
            }
        });

    }

    private ArrayList<String> collectReviews(Map<String, Object> reviews) {
        ArrayList<String> revs = new ArrayList<>();
        if (reviews != null) {
            for (Map.Entry<String, Object> entry : reviews.entrySet()) {
                Map oneRev = (Map) entry.getValue();
                revs.add((String) oneRev.get("reviews"));
            }
        }
        return revs;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

}
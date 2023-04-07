package com.example.hopreviews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.hopreviews.databinding.ActivityLocationBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationActivity extends AppCompatActivity {

    ActivityLocationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("reviews");
        HashMap<String, List<String>> reviews = new HashMap<>();
        ref.child(getIntent().getStringExtra("name")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> reviews = collectReviews((Map<String, Object>) snapshot.getValue());
                ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_location, reviews);
                ListView listView = (ListView) findViewById(R.id.reviewlist);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
}
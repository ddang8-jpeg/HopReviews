package com.example.hopreviews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.hopreviews.databinding.ActivityAddReviewBinding;
import com.example.hopreviews.databinding.ActivityProfileBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;

public class AddReviewActivity extends AppCompatActivity {
    ActivityAddReviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Add Review for " + getIntent().getStringExtra("name"));
        }
        Button button = findViewById(R.id.post);
        RatingBar rb = findViewById(R.id.ratingbar);
        EditText reviewText = findViewById(R.id.reviewText);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("locations").child(getIntent().getStringExtra("name")).child("reviews");
        button.setOnClickListener(v -> {
            String review = reviewText.getText().toString();
            if (review == null || review.isEmpty()) {
                Toast toast = Toast.makeText(getApplicationContext(), "Review is empty.", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            String username = getIntent().getStringExtra("username");
            String timestamp = String.valueOf(System.currentTimeMillis());
            String rating = String.valueOf(rb.getRating());
            ref.child(encodeEmail(username)).child(timestamp).child("review").setValue(review);
            ref.child(encodeEmail(username)).child(timestamp).child("rating").setValue(rating);
            Intent intent = new Intent();
            intent.putExtra("username", username);
            intent.putExtra("newlyadded", review);
            intent.putExtra("timestamp", timestamp);
            intent.putExtra("rating", rating);
            setResult(0, intent);
            this.finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        setResult(1);
        this.finish();
        return super.onOptionsItemSelected(item);
    }

    private String encodeEmail(String str) {
        str = str.replaceAll("@", "-");
        str = str.replaceAll("\\.", "_");
        return str;
    }
}
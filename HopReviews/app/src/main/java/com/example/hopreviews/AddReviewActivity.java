package com.example.hopreviews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
        EditText reviewText = findViewById(R.id.reviewText);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("reviews").child(getIntent().getStringExtra("name"));
        button.setOnClickListener(v -> {
            String review = reviewText.getText().toString();
            if (review == null || review.isEmpty()) {
                Toast toast = Toast.makeText(getApplicationContext(), "Review is empty.", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            String username = getIntent().getStringExtra("username");
            ref.child(encodeEmail(username)).child(String.valueOf(System.currentTimeMillis())).setValue(review);
            this.finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

    private String encodeEmail(String str) {
        str = str.replaceAll("@", "-");
        str = str.replaceAll("\\.", "_");
        return str;
    }
}
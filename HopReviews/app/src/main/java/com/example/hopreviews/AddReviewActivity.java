package com.example.hopreviews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.hopreviews.databinding.ActivityAddReviewBinding;
import com.example.hopreviews.databinding.ActivityProfileBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddReviewActivity extends AppCompatActivity {
    ActivityAddReviewBinding binding;
    int SELECT_PHOTO = 200;
    Uri photo;

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
        String timestamp = String.valueOf(System.currentTimeMillis());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refLocations = database.getReference("locations").child(getIntent().getStringExtra("name")).child("reviews");
        DatabaseReference refFeed = database.getReference("feed").child(timestamp);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        button.setOnClickListener(v -> {
            String review = reviewText.getText().toString();
            if (review == null || review.isEmpty()) {
                Toast toast = Toast.makeText(getApplicationContext(), "Review is empty.", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            String username = getIntent().getStringExtra("username");
            String rating = String.valueOf(rb.getRating());
            refLocations.child(encodeEmail(username)).child(timestamp).child("review").setValue(review);
            refLocations.child(encodeEmail(username)).child(timestamp).child("rating").setValue(rating);
            refFeed.child(getIntent().getStringExtra("name")).child(encodeEmail(username)).child("review").setValue(review);
            refFeed.child(getIntent().getStringExtra("name")).child(encodeEmail(username)).child("rating").setValue(rating);
            Intent intent = new Intent();
            intent.putExtra("username", username);
            intent.putExtra("newlyadded", review);
            intent.putExtra("timestamp", timestamp);
            intent.putExtra("rating", rating);
            setResult(0, intent);
            if (photo != null) {
                storageReference.child(getIntent().getStringExtra("name")).child("images/" + photo.getLastPathSegment()).putFile(photo);
            }
            this.finish();
        });
        Button imgButton = binding.selectPhoto;
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Photo"), SELECT_PHOTO);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PHOTO) {
                photo = data.getData();
                if (photo != null) {
                    ImageView preview = binding.imageView;
                    preview.setImageURI(photo);
                }
            }
        }
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
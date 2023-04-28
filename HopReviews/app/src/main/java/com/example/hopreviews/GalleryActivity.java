package com.example.hopreviews;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hopreviews.adapter.ItemAdapter;
import com.example.hopreviews.data.model.Image;
import com.example.hopreviews.databinding.ActivityGalleryBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
    ActivityGalleryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("name") + " photos");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference();
        ArrayList<Image> imageList = new ArrayList<>();
        ItemAdapter images = new ItemAdapter(getApplicationContext(), imageList);
        RecyclerView recyclerView = binding.recyclerView;

        ref.child(getIntent().getStringExtra("name")).child("images").listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference file: listResult.getItems()) {
                        file.getDownloadUrl()
                                .addOnSuccessListener(uri -> imageList.add(new Image(uri)))
                                .addOnSuccessListener(uri -> recyclerView.setAdapter(images));
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }
}

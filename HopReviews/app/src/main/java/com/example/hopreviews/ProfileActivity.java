package com.example.hopreviews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.hopreviews.databinding.ActivityProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private SharedPreferences sharedPreferences;
    private TextView firstLastNameText;
    private TextView yearText;
    private TextView emailText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        sharedPreferences = getSharedPreferences("email", Context.MODE_PRIVATE);
        firstLastNameText = findViewById(R.id.firstLastName);
        emailText = findViewById(R.id.email);
        yearText = findViewById(R.id.year);
        String userEmail = sharedPreferences.getString("email", "");
        String em = encodeEmail(userEmail);
        DatabaseReference ref = database.getReference("users").child(em);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String firstName = (String) snapshot.child("firstName").getValue();
                String lastName = (String) snapshot.child("lastName").getValue();
                String year = (String) snapshot.child("year").getValue();
                String name = firstName + " " + lastName;
                String final_email = userEmail + "@jhu.edu";
                firstLastNameText.setText(name);
                yearText.setText(year);
                emailText.setText(final_email);
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
            startActivity(intent);
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
}
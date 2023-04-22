package com.example.hopreviews;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hopreviews.databinding.ActivityEditProfileBinding;
import com.example.hopreviews.databinding.ActivityProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private SharedPreferences sharedPreferences;
    private String originalEmail;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        sharedPreferences = getSharedPreferences("email", Context.MODE_PRIVATE);
        EditText firstNameEditText = binding.editFirstName;
        EditText lastNameEditText = binding.editLastName;
        Spinner yearSpinner = binding.spinner;
        EditText emailEditText = binding.editEmail;
        Button saveChanges = binding.save;
        String userEmail = sharedPreferences.getString("email", "");
        String em = encodeEmail(userEmail);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users").child(em);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fName = (String) snapshot.child("firstName").getValue();
                String lName = (String) snapshot.child("lastName").getValue();
                String yr = (String) snapshot.child("year").getValue();
                String registered_email = (String) snapshot.child("email").getValue();
                originalEmail = registered_email;
                firstNameEditText.setText(fName);
                lastNameEditText.setText(lName);
                emailEditText.setText(registered_email);
                yearSpinner.setSelection(yr.equals("Freshman") ? 0 : yr.equals("Sophomore") ? 1 :
                        yr.equals("Junior") ? 2 : yr.equals("Senior") ? 3 : 4);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("loadPost:onCancelled", databaseError.toException());
            }
        };
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_entries,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);
        saveChanges.setOnClickListener(v -> {
            String fn = firstNameEditText.getText().toString();
            String ln = lastNameEditText.getText().toString();
            String yr = yearSpinner.getSelectedItem().toString();
            String entered_email = emailEditText.getText().toString();

            if (!fn.isEmpty() && !ln.isEmpty() && !yr.isEmpty() && !entered_email.isEmpty()) {
                if (!isUserNameValid(entered_email)) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.invalid_username, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    ref.child("email").get().addOnCompleteListener(task -> {
                        if (entered_email.equals(originalEmail)) {
                            ref.child("firstName").setValue(fn);
                            ref.child("lastName").setValue(ln);
                            ref.child("year").setValue(yr);
                            Toast toast = Toast.makeText(getApplicationContext(), "Save successful!", Toast.LENGTH_SHORT);
                            toast.show();
                            this.finish();
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "You cannot change emails.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid Info", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ref.addValueEventListener(listener);
    }

    private String encodeEmail(String str) {
        str = str.replaceAll("@", "-");
        str = str.replaceAll("\\.", "_");
        return str;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        this.finish();
        return super.onOptionsItemSelected(item);
    }

    private boolean isUserNameValid(String username) {
        if (username == null || !username.contains("@jhu.edu")) {
            return false;
        }
        if (username.contains("@jhu.edu")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }
}

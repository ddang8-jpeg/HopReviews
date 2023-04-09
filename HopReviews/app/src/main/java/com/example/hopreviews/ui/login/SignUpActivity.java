package com.example.hopreviews.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hopreviews.R;
import com.example.hopreviews.databinding.ActivitySignUpBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final EditText firstName = binding.firstname;
        final EditText lastName = binding.lastname;
        final Spinner year = binding.spinner;
        final EditText email = binding.email;
        final EditText password = binding.newpassword;
        sharedPreferences = getSharedPreferences("email", Context.MODE_PRIVATE);


        Button signUp = binding.signup;

        signUp.setOnClickListener(v -> {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("users");
            String fn = firstName.getText().toString();
            String ln = lastName.getText().toString();
            String yr = year.getSelectedItem().toString();
            String emOriginal = email.getText().toString();
            String em = encodeEmail(emOriginal);
            String pw = password.getText().toString();

            if (!fn.isEmpty() && !ln.isEmpty() && !yr.isEmpty() && !em.isEmpty() && !pw.isEmpty()) {
                if (!isUserNameValid(emOriginal)) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.invalid_username, Toast.LENGTH_SHORT);
                    toast.show();
                } else if (pw.length() <= 5) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.invalid_password, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    ref.child(em).child("email").get().addOnCompleteListener(task -> {
                        if (task.getResult().getValue() == null) {
                            ref.child(em).child("firstName").setValue(fn);
                            ref.child(em).child("lastName").setValue(ln);
                            ref.child(em).child("year").setValue(yr);
                            ref.child(em).child("email").setValue(emOriginal);
                            ref.child(em).child("password").setValue(pw);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("email", emOriginal);
                            editor.apply();
                            Toast toast = Toast.makeText(getApplicationContext(), "Sign up successful!", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "This user already exists.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid Info", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        Button alreadyUser = binding.alreadyuser;
        alreadyUser.setOnClickListener(v -> this.finish());
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
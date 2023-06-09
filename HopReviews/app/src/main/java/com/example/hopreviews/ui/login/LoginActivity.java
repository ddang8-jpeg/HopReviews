package com.example.hopreviews.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hopreviews.MainActivity;
import com.example.hopreviews.R;
import com.example.hopreviews.databinding.ActivityLoginBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;

    private SharedPreferences emailSharedPreferences;
    private SharedPreferences rememberMeSharedPreferences;
    private boolean remember;
    private CheckBox rememberMe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        rememberMe = findViewById(R.id.checkBox);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        emailSharedPreferences = getSharedPreferences("email", Context.MODE_PRIVATE);
        rememberMeSharedPreferences = getSharedPreferences("rememberMe", Context.MODE_PRIVATE);
        remember = rememberMeSharedPreferences.getBoolean("rememberMe", false);

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
            }
            setResult(Activity.RESULT_OK);

            //Complete and destroy login activity once successful
            loginSuccess();
        });

        if (remember) {
            String user = emailSharedPreferences.getString("email", "");
            String pw = rememberMeSharedPreferences.getString("password", "");
            loginViewModel.login(user, pw);
        }

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("users");
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String encoded = encodeEmail(username);
            ref.child(encoded).child("password").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String pw = snapshot.getValue(String.class);
                    if (pw != null) {
                        if (pw.equals(password)) {
                            loginViewModel.login(username, password);
                            SharedPreferences.Editor editor = emailSharedPreferences.edit();
                            editor.putString("email", username);
                            editor.apply();
                            SharedPreferences.Editor remember_editor = rememberMeSharedPreferences.edit();
                            if (rememberMe.isChecked()) {
                                remember_editor.putString("password", password);
                                remember_editor.putBoolean("rememberMe", true);
                            } else {
                                remember_editor.putString("password", "");
                                remember_editor.putBoolean("rememberMe", false);
                            }
                            remember_editor.apply();

                        } else {
                            loadingProgressBar.setVisibility(View.INVISIBLE);
                            Toast toast = Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } else {
                        loadingProgressBar.setVisibility(View.INVISIBLE);
                        Toast toast = Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });

        final Button signUpButton = binding.signupbutton;
        if (signUpButton != null) {
            signUpButton.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            });
        }
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName() + "!";
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    public void loginSuccess() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("username", binding.username.getText().toString());
        startActivity(intent);
    }

    private String encodeEmail(String str) {
        str = str.replaceAll("@", "-");
        str = str.replaceAll("\\.", "_");
        return str;
    }
}
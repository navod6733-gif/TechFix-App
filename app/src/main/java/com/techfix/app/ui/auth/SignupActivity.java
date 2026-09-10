package com.techfix.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.data.local.AppDatabase;
import com.techfix.app.data.local.entities.AppUser;
import com.techfix.app.util.PasswordUtils;

import java.util.UUID;
import java.util.concurrent.Executors;

public class SignupActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, phoneInput, passwordInput;
    private Button signupButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        signupButton = findViewById(R.id.signupButton);
        progressBar = findViewById(R.id.signupProgress);

        signupButton.setOnClickListener(v -> attemptSignup());
    }

    private void attemptSignup() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameInput.setError("Name is required");
            return;
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Valid email is required");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            phoneInput.setError("Phone number is required");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return;
        }

        setLoading(true);

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            AppUser existing = db.appUserDao().getUserByEmail(email);

            runOnUiThread(() -> {
                if (existing != null) {
                    setLoading(false);
                    emailInput.setError("An account with this email already exists");
                    return;
                }

                Executors.newSingleThreadExecutor().execute(() -> {
                    AppUser user = new AppUser();
                    user.id = UUID.randomUUID().toString();
                    user.name = name;
                    user.email = email;
                    user.phone = phone;
                    user.passwordHash = PasswordUtils.hash(password);

                    db.appUserDao().insert(user);

                    runOnUiThread(() -> {
                        setLoading(false);
                        Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    });
                });
            });
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        signupButton.setEnabled(!loading);
    }
}
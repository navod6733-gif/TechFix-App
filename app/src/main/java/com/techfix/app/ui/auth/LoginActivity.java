package com.techfix.app.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.MainActivity;
import com.techfix.app.R;
import com.techfix.app.data.local.AppDatabase;
import com.techfix.app.data.local.entities.AppUser;
import com.techfix.app.util.PasswordUtils;

import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView goToSignup;
    private ProgressBar progressBar;

    public static final String PREFS_NAME = "techfix_prefs";
    public static final String KEY_USER_ID = "logged_in_user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        goToSignup = findViewById(R.id.goToSignup);
        progressBar = findViewById(R.id.loginProgress);

        loginButton.setOnClickListener(v -> attemptLogin());
        goToSignup.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String email = emailInput.getText().toString().trim();
        if (prefs.contains(KEY_USER_ID)) {
            goToMain(email);
        }
    }

    private void attemptLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            return;
        }

        setLoading(true);
        String hashedPassword = PasswordUtils.hash(password);

        Executors.newSingleThreadExecutor().execute(() -> {
            AppUser user = AppDatabase.getInstance(getApplicationContext())
                    .appUserDao().login(email, hashedPassword);

            runOnUiThread(() -> {
                setLoading(false);
                if (user != null) {
                    getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                            .edit()
                            .putString(KEY_USER_ID, user.id)
                            .apply();
                    goToMain(email);
                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void goToMain(String email) {

        if (email.equals("admin@admin.com")) {
            startActivity(new Intent(this, com.techfix.app.ui.admin.AdminActivity.class));
            finish();
        }
        else {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!loading);
    }
}
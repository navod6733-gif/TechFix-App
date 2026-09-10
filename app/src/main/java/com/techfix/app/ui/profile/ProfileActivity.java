package com.techfix.app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.data.local.AppDatabase;
import com.techfix.app.data.local.entities.AppUser;
import com.techfix.app.ui.auth.LoginActivity;
import com.techfix.app.util.PasswordUtils;

import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {

    private EditText nameInput, phoneInput;
    private TextView emailText;
    private Button saveButton, changePasswordButton, logoutButton;
    private ProgressBar progressBar;

    private AppDatabase db;
    private AppUser currentUser;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = AppDatabase.getInstance(getApplicationContext());
        userId = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
                .getString(LoginActivity.KEY_USER_ID, null);

        nameInput = findViewById(R.id.profileNameInput);
        emailText = findViewById(R.id.profileEmailText);
        phoneInput = findViewById(R.id.profilePhoneInput);
        saveButton = findViewById(R.id.saveProfileButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        logoutButton = findViewById(R.id.profileLogoutButton);
        progressBar = findViewById(R.id.profileProgress);

        if (userId == null) {
            goToLogin();
            return;
        }

        loadProfile();

        saveButton.setOnClickListener(v -> saveProfile());
        changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());
        logoutButton.setOnClickListener(v -> logout());
    }

    private void loadProfile() {
        Executors.newSingleThreadExecutor().execute(() -> {
            currentUser = db.appUserDao().getUserById(userId);
            runOnUiThread(() -> {
                if (currentUser != null) {
                    nameInput.setText(currentUser.name);
                    emailText.setText(currentUser.email);
                    phoneInput.setText(currentUser.phone);
                }
            });
        });
    }

    private void saveProfile() {
        String newName = nameInput.getText().toString().trim();
        String newPhone = phoneInput.getText().toString().trim();

        if (TextUtils.isEmpty(newName)) {
            nameInput.setError("Name can't be empty");
            return;
        }
        if (TextUtils.isEmpty(newPhone)) {
            phoneInput.setError("Phone can't be empty");
            return;
        }

        setLoading(true);

        Executors.newSingleThreadExecutor().execute(() -> {
            currentUser.name = newName;
            currentUser.phone = newPhone;
            db.appUserDao().update(currentUser);

            runOnUiThread(() -> {
                setLoading(false);
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_change_password, null);
        EditText currentPasswordInput = dialogView.findViewById(R.id.currentPasswordInput);
        EditText newPasswordInput = dialogView.findViewById(R.id.newPasswordInput);

        new AlertDialog.Builder(this)
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String currentPassword = currentPasswordInput.getText().toString().trim();
                    String newPassword = newPasswordInput.getText().toString().trim();

                    if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword)) {
                        Toast.makeText(this, "Both fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newPassword.length() < 6) {
                        Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    changePassword(currentPassword, newPassword);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void changePassword(String currentPassword, String newPassword) {
        String currentHash = PasswordUtils.hash(currentPassword);

        if (!currentHash.equals(currentUser.passwordHash)) {
            Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            currentUser.passwordHash = PasswordUtils.hash(newPassword);
            db.appUserDao().update(currentUser);

            runOnUiThread(() ->
                    Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show());
        });
    }

    private void logout() {
        getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
                .edit()
                .remove(LoginActivity.KEY_USER_ID)
                .apply();
        goToLogin();
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        saveButton.setEnabled(!loading);
    }
}
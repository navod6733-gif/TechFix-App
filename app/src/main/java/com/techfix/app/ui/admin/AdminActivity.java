package com.techfix.app.ui.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.techfix.app.R;
import com.techfix.app.ui.auth.LoginActivity;

public class AdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        findViewById(R.id.btnManageTechnicians).setOnClickListener(v ->
                startActivity(new Intent(this, ManageTechniciansActivity.class)));

        findViewById(R.id.btnManageParts).setOnClickListener(v ->
                startActivity(new Intent(this, ManageSparePartsActivity.class)));

        findViewById(R.id.btnAssignRepairs).setOnClickListener(v ->
                startActivity(new Intent(this, AssignRepairsActivity.class)));


        findViewById(R.id.btnLogout1).setOnClickListener(v -> {
            getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
                    .edit()
                    .remove(LoginActivity.KEY_USER_ID)
                    .apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
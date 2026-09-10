package com.techfix.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.techfix.app.data.local.AppDatabase
import com.techfix.app.data.local.DataSeeder
import com.techfix.app.ui.auth.LoginActivity
import com.techfix.app.ui.branches.BranchListActivity
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var welcomeText: TextView
    private var currentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DataSeeder.seedIfEmpty(applicationContext)

        val prefs = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
        currentUserId = prefs.getString(LoginActivity.KEY_USER_ID, null)

        welcomeText = findViewById(R.id.welcomeText)
        loadUserName()

        findViewById<com.google.android.material.card.MaterialCardView>(R.id.btnBranches).setOnClickListener {
            startActivity(Intent(this, BranchListActivity::class.java))
        }


        findViewById<com.google.android.material.card.MaterialCardView>(R.id.btnBookRepair).setOnClickListener {
            startActivity(Intent(this, com.techfix.app.ui.booking.BookingActivity::class.java))

        }

        findViewById<com.google.android.material.card.MaterialCardView>(R.id.btnTrackRepair).setOnClickListener {
            startActivity(Intent(this, com.techfix.app.ui.tracking.TrackingActivity::class.java))

        }

        findViewById<com.google.android.material.card.MaterialCardView>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, com.techfix.app.ui.profile.ProfileActivity::class.java))
        }


        findViewById<com.google.android.material.card.MaterialCardView>(R.id.btnPayments).setOnClickListener {
            startActivity(Intent(this, com.techfix.app.ui.payment.PaymentsActivity::class.java))
        }

        findViewById<android.widget.Button>(R.id.btnLogout).setOnClickListener {
            logout()
        }
    }

    private fun loadUserName() {
        val userId = currentUserId ?: return
        Executors.newSingleThreadExecutor().execute {
            val user = AppDatabase.getInstance(applicationContext).appUserDao().getUserById(userId)
            if (user != null) {
                runOnUiThread { welcomeText.text = "Welcome back, ${user.name}" }
            }
        }
    }

    private fun logout() {
        getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE)
            .edit()
            .remove(LoginActivity.KEY_USER_ID)
            .apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
package com.example.mealmate

import android.content.SharedPreferences
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {

    private lateinit var notificationSwitch: SwitchCompat
    private lateinit var preferences: SharedPreferences

    companion object {
        private const val PREFS_NAME = "mealmate_settings"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImmersiveMode.enable(this)
        setContentView(R.layout.activity_settings)

        preferences = getSharedPreferences(
            PREFS_NAME,
            MODE_PRIVATE
        )

        val btnBack = findViewById<Button>(R.id.btnSettingsBack)
        val tvAbout = findViewById<TextView>(R.id.tvSettingsAbout)
        val tvPrivacy = findViewById<TextView>(R.id.tvSettingsPrivacy)

        notificationSwitch = findViewById(R.id.switchNotifications)

        // Restore the saved preference.
        // Notifications are enabled by default.
        notificationSwitch.isChecked = preferences.getBoolean(
            KEY_NOTIFICATIONS_ENABLED,
            true
        )

        // Save the preference whenever the switch changes.
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->

            preferences.edit()
                .putBoolean(KEY_NOTIFICATIONS_ENABLED, isChecked)
                .apply()

            val message = if (isChecked) {
                "Notification preference enabled"
            } else {
                "Notification preference disabled"
            }

            Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }

        // Return to Profile.
        btnBack.setOnClickListener {
            finish()
        }

        // About MealMate.
        // Open About MealMate screen.
        tvAbout.setOnClickListener {
            startActivity(
                Intent(this, AboutActivity::class.java)
            )
        }

        // Privacy Policy.
        // Open Privacy Policy screen.
        tvPrivacy.setOnClickListener {
            startActivity(
                Intent(this, PrivacyPolicyActivity::class.java)
            )
        }
    }
}
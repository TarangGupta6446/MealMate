package com.example.mealmate

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class PrivacyPolicyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImmersiveMode.enable(this)
        setContentView(R.layout.activity_privacy_policy)

        val btnBack = findViewById<Button>(R.id.btnPrivacyBack)

        btnBack.setOnClickListener {
            finish()
        }
    }
}
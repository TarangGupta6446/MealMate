package com.example.mealmate

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImmersiveMode.enable(this)
        setContentView(R.layout.activity_about)

        val btnBack = findViewById<Button>(R.id.btnAboutBack)

        btnBack.setOnClickListener {
            finish()
        }
    }
}
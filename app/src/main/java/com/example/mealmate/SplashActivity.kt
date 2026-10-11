package com.example.mealmate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    private val navigateRunnable = Runnable {
        val currentUser = FirebaseAuth.getInstance().currentUser

        val nextActivity = if (currentUser != null) {
            HomeActivity::class.java
        } else {
            LoginActivity::class.java
        }

        startActivity(Intent(this, nextActivity))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        CartManager.initialize(applicationContext)
        super.onCreate(savedInstanceState)

        ImmersiveMode.enable(this)
        setContentView(R.layout.activity_splash)

        handler.postDelayed(navigateRunnable, 2000)
    }

    override fun onDestroy() {
        handler.removeCallbacks(navigateRunnable)
        super.onDestroy()
    }
}
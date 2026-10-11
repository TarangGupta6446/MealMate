package com.example.mealmate

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : BaseNavigationActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tvSettings: TextView
    private lateinit var db: FirebaseFirestore

    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileEmail: TextView
    private lateinit var tvLogout: TextView
    private lateinit var tvEditProfile: TextView
    private lateinit var tvAddress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImmersiveMode.enable(this)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        tvProfileName = findViewById(R.id.tvProfileName)
        tvProfileEmail = findViewById(R.id.tvProfileEmail)
        tvLogout = findViewById(R.id.tvLogout)
        tvEditProfile = findViewById(R.id.tvEditProfile)
        tvAddress = findViewById(R.id.tvAddress)
        tvSettings = findViewById(R.id.tvSettings)

        tvSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Shared bottom navigation
        setupBottomNavigation("profile")

        val currentUser = auth.currentUser

        if (currentUser == null) {
            goToLogin()
            return
        }

        tvProfileEmail.text = currentUser.email ?: ""

        // Open Edit Profile
        tvEditProfile.setOnClickListener {
            startActivity(
                Intent(this, EditProfileActivity::class.java)
            )
        }

        // Open Delivery Address
        tvAddress.setOnClickListener {
            startActivity(
                Intent(this, DeliveryAddressActivity::class.java)
            )
        }

        // Logout
        tvLogout.setOnClickListener {
            auth.signOut()

            Toast.makeText(
                this,
                "Logged out successfully",
                Toast.LENGTH_SHORT
            ).show()

            goToLogin()
        }
    }

    // Refresh profile details whenever this screen becomes visible
    override fun onResume() {
        super.onResume()

        if (!::auth.isInitialized || !::db.isInitialized) {
            return
        }

        val currentUser = auth.currentUser

        if (currentUser == null) {
            goToLogin()
            return
        }

        tvProfileEmail.text = currentUser.email ?: ""

        db.collection("users")
            .document(currentUser.uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {
                    tvProfileName.text =
                        document.getString("name") ?: "MealMate User"
                } else {
                    tvProfileName.text = "MealMate User"
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Could not refresh profile",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}
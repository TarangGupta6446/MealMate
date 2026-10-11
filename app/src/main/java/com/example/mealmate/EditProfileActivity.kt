package com.example.mealmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var etEditName: EditText
    private lateinit var tvEditEmail: TextView
    private lateinit var btnSaveProfile: Button
    private lateinit var btnCancelEdit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersiveMode.enable(this)
        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etEditName = findViewById(R.id.etEditName)
        tvEditEmail = findViewById(R.id.tvEditEmail)
        btnSaveProfile = findViewById(R.id.btnSaveProfile)
        btnCancelEdit = findViewById(R.id.btnCancelEdit)

        val user = auth.currentUser

        if (user == null) {
            goToLogin()
            return
        }

        // Display the email from Firebase Authentication.
        tvEditEmail.text = user.email ?: ""

        // Load the current name from Firestore.
        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    etEditName.setText(
                        document.getString("name") ?: ""
                    )
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Could not load your profile.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        // Save the updated name.
        btnSaveProfile.setOnClickListener {
            saveProfile(user.uid)
        }

        // Cancel and return to Profile.
        btnCancelEdit.setOnClickListener {
            finish()
        }
    }

    private fun saveProfile(userId: String) {
        val updatedName = etEditName.text.toString().trim()

        if (updatedName.isEmpty()) {
            etEditName.error = "Please enter your name"
            etEditName.requestFocus()
            return
        }

        if (updatedName.length < 2) {
            etEditName.error = "Name must contain at least 2 characters"
            etEditName.requestFocus()
            return
        }

        btnSaveProfile.isEnabled = false
        btnSaveProfile.text = "Saving..."

        db.collection("users")
            .document(userId)
            .update("name", updatedName)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Profile updated successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, ProfileActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP

                startActivity(intent)
                finish()
            }
            .addOnFailureListener { error ->
                btnSaveProfile.isEnabled = true
                btnSaveProfile.text = "Save Changes"

                Toast.makeText(
                    this,
                    "Could not update profile: ${
                        error.localizedMessage ?: "Please try again."
                    }",
                    Toast.LENGTH_LONG
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
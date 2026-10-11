package com.example.mealmate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersiveMode.enable(this)

        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)

        btnRegister.setOnClickListener {

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (name.isEmpty()) {
                etName.error = "Enter your name"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                etEmail.error = "Enter your email"
                return@setOnClickListener
            }

            if (password.length < 6) {
                etPassword.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            btnRegister.isEnabled = false
            btnRegister.text = "Creating Account..."

            Toast.makeText(
                this,
                "Connecting to Firebase...",
                Toast.LENGTH_SHORT
            ).show()

            // Timeout protection
            val timeoutRunnable = Runnable {

                btnRegister.isEnabled = true
                btnRegister.text = "Register"

                Toast.makeText(
                    this,
                    "Firebase connection timed out. Check internet/Firebase setup.",
                    Toast.LENGTH_LONG
                ).show()
            }

            handler.postDelayed(timeoutRunnable, 15000)

            auth.createUserWithEmailAndPassword(
                email,
                password
            ).addOnCompleteListener { task ->

                handler.removeCallbacks(timeoutRunnable)

                if (task.isSuccessful) {

                    val user = auth.currentUser

                    if (user == null) {

                        btnRegister.isEnabled = true
                        btnRegister.text = "Register"

                        Toast.makeText(
                            this,
                            "Firebase account was not found.",
                            Toast.LENGTH_LONG
                        ).show()

                        return@addOnCompleteListener
                    }

                    val userId = user.uid

                    val userData = hashMapOf(
                        "name" to name,
                        "email" to email
                    )

                    db.collection("users")
                        .document(userId)
                        .set(userData)
                        .addOnSuccessListener {

                            Toast.makeText(
                                this,
                                "Registration successful!",
                                Toast.LENGTH_LONG
                            ).show()

                            val intent = Intent(
                                this,
                                LoginActivity::class.java
                            )

                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { error ->

                            btnRegister.isEnabled = true
                            btnRegister.text = "Register"

                            Toast.makeText(
                                this,
                                "Firestore error:\n${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                } else {

                    btnRegister.isEnabled = true
                    btnRegister.text = "Register"

                    val error =
                        task.exception?.message
                            ?: "Unknown Firebase error"

                    Toast.makeText(
                        this,
                        "Firebase error:\n$error",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        tvLogin.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )

            finish()
        }
    }
}
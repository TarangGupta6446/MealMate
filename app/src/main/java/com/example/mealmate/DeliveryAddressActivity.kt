package com.example.mealmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DeliveryAddressActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var etDeliveryAddress: EditText
    private lateinit var etDeliveryPhone: EditText
    private lateinit var btnSaveAddress: Button
    private lateinit var btnCancelAddress: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImmersiveMode.enable(this)
        setContentView(R.layout.activity_delivery_address)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etDeliveryAddress = findViewById(R.id.etDeliveryAddress)
        etDeliveryPhone = findViewById(R.id.etDeliveryPhone)
        btnSaveAddress = findViewById(R.id.btnSaveAddress)
        btnCancelAddress = findViewById(R.id.btnCancelAddress)

        val user = auth.currentUser

        if (user == null) {
            goToLogin()
            return
        }

        // Load the previously saved address and phone number
        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                etDeliveryAddress.setText(
                    document.getString("address") ?: ""
                )

                etDeliveryPhone.setText(
                    document.getString("phone") ?: ""
                )
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Could not load saved address.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        // Save address and phone number
        btnSaveAddress.setOnClickListener {
            saveAddress(user.uid)
        }

        // Return without saving
        btnCancelAddress.setOnClickListener {
            finish()
        }
    }

    private fun saveAddress(userId: String) {

        val address = etDeliveryAddress.text.toString().trim()
        val phone = etDeliveryPhone.text.toString().trim()

        if (address.isEmpty()) {
            etDeliveryAddress.error = "Please enter your delivery address"
            etDeliveryAddress.requestFocus()
            return
        }

        if (address.length < 5) {
            etDeliveryAddress.error = "Please enter a complete address"
            etDeliveryAddress.requestFocus()
            return
        }

        if (!phone.matches(Regex("^[0-9]{10}$"))) {
            etDeliveryPhone.error = "Enter a valid 10-digit phone number"
            etDeliveryPhone.requestFocus()
            return
        }

        btnSaveAddress.isEnabled = false
        btnSaveAddress.text = "Saving..."

        val addressData = mapOf(
            "address" to address,
            "phone" to phone
        )

        db.collection("users")
            .document(userId)
            .update(addressData)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Delivery address saved successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            .addOnFailureListener { error ->
                btnSaveAddress.isEnabled = true
                btnSaveAddress.text = "Save Address"

                Toast.makeText(
                    this,
                    "Could not save address: ${
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
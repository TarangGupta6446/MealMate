package com.example.mealmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var btnPlaceOrder: Button
    private lateinit var etAddress: EditText
    private lateinit var etPhone: EditText

    private lateinit var tvOrderItems: TextView
    private lateinit var tvOrderTotal: TextView
    private lateinit var rgPayment: RadioGroup

    private val orderRepository = FirebaseOrderRepository()

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImmersiveMode.enable(this)
        setContentView(R.layout.activity_checkout)

        etAddress = findViewById(R.id.etAddress)
        etPhone = findViewById(R.id.etPhone)

        tvOrderItems = findViewById(R.id.tvOrderItems)
        tvOrderTotal = findViewById(R.id.tvOrderTotal)

        rgPayment = findViewById(R.id.rgPayment)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)

        displayOrderSummary()
        loadSavedAddress()

        btnPlaceOrder.setOnClickListener {
            placeOrder()
        }
    }

    private fun loadSavedAddress() {

        val user = auth.currentUser ?: return

        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val savedAddress =
                        document.getString("address") ?: ""

                    val savedPhone =
                        document.getString("phone") ?: ""

                    if (etAddress.text.toString().isBlank()) {
                        etAddress.setText(savedAddress)
                    }

                    if (etPhone.text.toString().isBlank()) {
                        etPhone.setText(savedPhone)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Could not load saved delivery details.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun displayOrderSummary() {

        val itemsText =
            CartManager.cartItems.joinToString("\n") {
                "${it.name} × ${it.quantity}"
            }

        tvOrderItems.text =
            if (itemsText.isBlank()) {
                "Your cart is empty"
            } else {
                itemsText
            }

        val subtotal = CartManager.getTotal()
        val deliveryFee = if (subtotal > 0) 40 else 0
        val total = subtotal + deliveryFee

        tvOrderTotal.text = "₹$total"
    }

    private fun placeOrder() {

        val address = etAddress.text.toString().trim()
        val phone = etPhone.text.toString().trim()

        if (CartManager.cartItems.isEmpty()) {
            Toast.makeText(
                this,
                "Your cart is empty",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (address.isEmpty()) {
            etAddress.error = "Enter delivery address"
            etAddress.requestFocus()
            return
        }

        if (!phone.matches(Regex("^[0-9]{10}$"))) {
            etPhone.error = "Enter a valid 10-digit phone number"
            etPhone.requestFocus()
            return
        }

        if (rgPayment.checkedRadioButtonId == -1) {
            Toast.makeText(
                this,
                "Please select a payment method",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val paymentMethod =
            if (rgPayment.checkedRadioButtonId == R.id.rbCash) {
                "Cash on Delivery"
            } else {
                "Online Payment"
            }

        val subtotal = CartManager.getTotal()
        val deliveryFee = 40
        val total = subtotal + deliveryFee

        val orderId =
            "MM${System.currentTimeMillis().toString().takeLast(8)}"

        val currentDate = SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            Locale.getDefault()
        ).format(Date())

        val foodItems =
            CartManager.cartItems.joinToString(", ") {
                "${it.name} × ${it.quantity}"
            }

        val order = Order(
            orderId = orderId,
            foodItems = foodItems,
            date = currentDate,
            amount = total,
            status = "Preparing"
        )

        btnPlaceOrder.isEnabled = false
        btnPlaceOrder.text = "Placing Order..."

        orderRepository.saveOrder(
            order = order,
            address = address,
            phone = phone,
            paymentMethod = paymentMethod,

            onSuccess = {

                CartManager.clearCart()

                Toast.makeText(
                    this,
                    "Order placed successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(
                    this,
                    OrderSuccessActivity::class.java
                )

                intent.putExtra("order_id", orderId)
                intent.putExtra("order_total", total)
                intent.putExtra("payment_method", paymentMethod)

                startActivity(intent)
                finish()
            },

            onFailure = { errorMessage ->

                btnPlaceOrder.isEnabled = true
                btnPlaceOrder.text = "PLACE ORDER"

                Toast.makeText(
                    this,
                    "Could not place order: $errorMessage",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
}
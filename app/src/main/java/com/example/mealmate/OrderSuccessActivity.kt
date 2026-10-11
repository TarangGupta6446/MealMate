package com.example.mealmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OrderSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImmersiveMode.enable(this)

        setContentView(R.layout.activity_order_success)

        val tvOrderId = findViewById<TextView>(R.id.tvOrderId)
        val tvDeliveryTime = findViewById<TextView>(R.id.tvDeliveryTime)

        val btnViewOrders = findViewById<Button>(R.id.btnViewOrders)
        val btnBackHome = findViewById<Button>(R.id.btnBackHome)

        // Get order information passed from CheckoutActivity
        val orderId = intent.getStringExtra("order_id") ?: "MM000000"
        val total = intent.getIntExtra("order_total", 0)

        // Display order details
        tvOrderId.text = "Order ID: $orderId"

        tvDeliveryTime.text =
            "Total Amount: ₹$total\nEstimated delivery: 30–40 minutes"

        // Open the orders screen
        btnViewOrders.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Return to the existing Home screen
        btnBackHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(intent)
            finish()
        }
    }
}
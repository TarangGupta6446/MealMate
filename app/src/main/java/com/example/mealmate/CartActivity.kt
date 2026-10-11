package com.example.mealmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : BaseNavigationActivity() {

    private lateinit var cartAdapter: CartAdapter

    private lateinit var tvSubtotal: TextView
    private lateinit var tvTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImmersiveMode.enable(this)
        setContentView(R.layout.activity_cart)

        val rvCart = findViewById<RecyclerView>(R.id.rvCart)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvTotal = findViewById(R.id.tvTotal)

        val btnCheckout = findViewById<Button>(R.id.btnCheckout)

        rvCart.layoutManager = LinearLayoutManager(this)

        cartAdapter = CartAdapter(CartManager.cartItems) { position ->

            CartManager.removeItem(position)

            cartAdapter.notifyItemRemoved(position)

            updateTotal()
        }

        rvCart.adapter = cartAdapter

        updateTotal()

        // Shared bottom navigation
        setupBottomNavigation("cart")

        btnCheckout.setOnClickListener {

            if (CartManager.cartItems.isNotEmpty()) {

                val intent = Intent(
                    this,
                    CheckoutActivity::class.java
                )

                startActivity(intent)

            } else {
                android.widget.Toast.makeText(
                    this,
                    "Your cart is empty",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (::cartAdapter.isInitialized) {
            cartAdapter.notifyDataSetChanged()
            updateTotal()
        }
    }

    private fun updateTotal() {

        val subtotal = CartManager.getTotal()

        val deliveryFee = if (subtotal > 0) 40 else 0

        val total = subtotal + deliveryFee

        tvSubtotal.text = "₹$subtotal"
        tvTotal.text = "₹$total"
    }
}
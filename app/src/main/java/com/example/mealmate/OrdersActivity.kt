package com.example.mealmate

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrdersActivity : BaseNavigationActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var rvCurrentOrders: RecyclerView
    private lateinit var rvPreviousOrders: RecyclerView

    private lateinit var tvCurrentOrders: TextView
    private lateinit var tvPreviousOrders: TextView
    private lateinit var tvEmptyOrders: TextView

    private val currentOrders = mutableListOf<Order>()
    private val previousOrders = mutableListOf<Order>()

    private lateinit var currentAdapter: OrderAdapter
    private lateinit var previousAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImmersiveMode.enable(this)
        setContentView(R.layout.activity_orders)
        findViewById<TextView>(R.id.btnBackOrders).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        rvCurrentOrders = findViewById(R.id.rvCurrentOrders)
        rvPreviousOrders = findViewById(R.id.rvPreviousOrders)
        tvCurrentOrders = findViewById(R.id.tvCurrentOrders)
        tvPreviousOrders = findViewById(R.id.tvPreviousOrders)

        rvCurrentOrders.layoutManager = LinearLayoutManager(this)
        rvPreviousOrders.layoutManager = LinearLayoutManager(this)

        currentAdapter = OrderAdapter(currentOrders)
        previousAdapter = OrderAdapter(previousOrders)

        rvCurrentOrders.adapter = currentAdapter
        rvPreviousOrders.adapter = previousAdapter

        // Empty-state message
        tvEmptyOrders = TextView(this).apply {
            text = "No orders yet.\nYour delicious meals will appear here!"
            textSize = 15f
            setTextColor(android.graphics.Color.rgb(120, 120, 120))
            gravity = Gravity.CENTER
            visibility = View.GONE
            setPadding(32, 32, 32, 32)
        }

        val root = rvCurrentOrders.parent as ViewGroup

        root.addView(
            tvEmptyOrders,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        // Shared bottom navigation
        setupBottomNavigation("orders")

        if (auth.currentUser == null) {
            goToLogin()
            return
        }

        loadOrders()
    }

    private fun loadOrders() {

        val userId = auth.currentUser?.uid

        if (userId == null) {
            goToLogin()
            return
        }

        db.collection("orders")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->

                currentOrders.clear()
                previousOrders.clear()

                for (document in documents) {

                    val order = Order(
                        orderId = document.getString("orderId")
                            ?: document.id,
                        foodItems = document.getString("foodItems") ?: "",
                        date = document.getString("date") ?: "",
                        amount = (document.getLong("amount") ?: 0L).toInt(),
                        status = document.getString("status") ?: "Preparing"
                    )

                    if (
                        order.status.equals("Delivered", ignoreCase = true) ||
                        order.status.equals("Cancelled", ignoreCase = true)
                    ) {
                        previousOrders.add(order)
                    } else {
                        currentOrders.add(order)
                    }
                }

                currentOrders.reverse()
                previousOrders.reverse()

                currentAdapter.notifyDataSetChanged()
                previousAdapter.notifyDataSetChanged()

                updateEmptyState()
            }
            .addOnFailureListener { error ->

                tvEmptyOrders.text =
                    "Unable to load orders.\n${error.localizedMessage ?: "Please try again."}"

                tvEmptyOrders.visibility = View.VISIBLE

                Toast.makeText(
                    this,
                    "Could not load orders: ${error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun updateEmptyState() {

        val noOrders =
            currentOrders.isEmpty() && previousOrders.isEmpty()

        tvEmptyOrders.visibility =
            if (noOrders) View.VISIBLE else View.GONE

        tvCurrentOrders.visibility =
            if (currentOrders.isNotEmpty()) View.VISIBLE else View.GONE

        rvCurrentOrders.visibility =
            if (currentOrders.isNotEmpty()) View.VISIBLE else View.GONE

        tvPreviousOrders.visibility =
            if (previousOrders.isNotEmpty()) View.VISIBLE else View.GONE

        rvPreviousOrders.visibility =
            if (previousOrders.isNotEmpty()) View.VISIBLE else View.GONE
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
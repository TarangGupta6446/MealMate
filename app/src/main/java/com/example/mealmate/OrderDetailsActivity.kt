package com.example.mealmate

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var tvDetailOrderId: TextView
    private lateinit var tvDetailStatus: TextView
    private lateinit var tvDetailDate: TextView
    private lateinit var tvDetailFoodItems: TextView
    private lateinit var tvDetailAddress: TextView
    private lateinit var tvDetailPhone: TextView
    private lateinit var tvDetailPayment: TextView
    private lateinit var tvDetailTotal: TextView

    private lateinit var btnBackToOrders: Button
    private lateinit var btnCancelOrder: Button

    private lateinit var trackingCard: View
    private lateinit var tvCancelledMessage: TextView

    private var currentOrderId: String? = null
    private var currentOrderStatus: String = "Unknown"

    private var cancellationInProgress = false

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val orange = Color.rgb(255, 107, 53)
    private val green = Color.rgb(46, 125, 50)
    private val grey = Color.rgb(130, 119, 115)

    private val paleOrange = Color.rgb(255, 240, 232)
    private val paleGreen = Color.rgb(232, 245, 233)
    private val paleGrey = Color.rgb(243, 237, 233)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImmersiveMode.enable(this)
        setContentView(R.layout.activity_order_details)

        // Initialize order detail views
        tvDetailOrderId = findViewById(R.id.tvDetailOrderId)
        tvDetailStatus = findViewById(R.id.tvDetailStatus)
        tvDetailDate = findViewById(R.id.tvDetailDate)
        tvDetailFoodItems = findViewById(R.id.tvDetailFoodItems)
        tvDetailAddress = findViewById(R.id.tvDetailAddress)
        tvDetailPhone = findViewById(R.id.tvDetailPhone)
        tvDetailPayment = findViewById(R.id.tvDetailPayment)
        tvDetailTotal = findViewById(R.id.tvDetailTotal)

        // Initialize buttons
        btnBackToOrders = findViewById(R.id.btnBackToOrders)
        btnCancelOrder = findViewById(R.id.btnCancelOrder)

        // Initialize tracking views
        trackingCard = findViewById(R.id.trackingCard)
        tvCancelledMessage = findViewById(R.id.tvCancelledMessage)

        // Back button
        btnBackToOrders.setOnClickListener {
            finish()
        }

        // Cancel order button
        btnCancelOrder.setOnClickListener {
            showCancelConfirmation()
        }

        loadOrderDetails()
    }

    // Load order details from Firestore
    private fun loadOrderDetails() {

        val user = auth.currentUser

        if (user == null) {
            goToLogin()
            return
        }

        val orderId = intent.getStringExtra("order_id")

        if (orderId.isNullOrBlank()) {
            Toast.makeText(
                this,
                "Order ID is missing.",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        db.collection("orders")
            .document(orderId)
            .get()
            .addOnSuccessListener { document ->

                if (!document.exists()) {
                    Toast.makeText(
                        this,
                        "Order not found.",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                    return@addOnSuccessListener
                }

                val orderUserId = document.getString("userId")

                if (orderUserId != user.uid) {
                    Toast.makeText(
                        this,
                        "You cannot view this order.",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                    return@addOnSuccessListener
                }

                currentOrderId = orderId

                val savedOrderId =
                    document.getString("orderId") ?: orderId

                currentOrderStatus =
                    document.getString("status") ?: "Unknown"

                val date =
                    document.getString("date") ?: "Not available"

                val foodItems =
                    document.getString("foodItems") ?: "Not available"

                val address =
                    document.getString("address") ?: "Not available"

                val phone =
                    document.getString("phone") ?: "Not available"

                val paymentMethod =
                    document.getString("paymentMethod")
                        ?: "Not recorded"

                val amount =
                    document.getLong("amount") ?: 0L

                // Display order information
                tvDetailOrderId.text = "Order ID: $savedOrderId"
                tvDetailStatus.text = "Status: $currentOrderStatus"
                tvDetailDate.text = "Ordered on $date"

                tvDetailFoodItems.text = foodItems
                tvDetailAddress.text = address
                tvDetailPhone.text = phone
                tvDetailPayment.text = paymentMethod
                tvDetailTotal.text = "₹$amount"

                // Update tracking and cancellation button
                updateTracking(currentOrderStatus)
                updateCancelButton(currentOrderStatus)
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    this,
                    "Could not load order: ${
                        error.localizedMessage ?: "Please try again."
                    }",
                    Toast.LENGTH_LONG
                ).show()

                finish()
            }
    }

    // Define which order statuses allow cancellation
    private fun isCancellableStatus(status: String): Boolean {

        return when (status.trim().lowercase()) {
            "order placed",
            "placed",
            "pending",
            "preparing",
            "confirmed" -> true

            else -> false
        }
    }

    // Show the Cancel Order button only when cancellation is allowed
    private fun updateCancelButton(status: String) {

        val canCancel = isCancellableStatus(status)

        btnCancelOrder.visibility =
            if (canCancel) View.VISIBLE else View.GONE

        btnCancelOrder.isEnabled =
            canCancel && !cancellationInProgress
    }

    // Ask the user before cancelling
    private fun showCancelConfirmation() {

        if (!isCancellableStatus(currentOrderStatus)) {
            Toast.makeText(
                this,
                "This order can no longer be cancelled.",
                Toast.LENGTH_LONG
            ).show()

            updateCancelButton(currentOrderStatus)
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Cancel Order?")
            .setMessage(
                "Are you sure you want to cancel this order? " +
                        "This action cannot be undone."
            )
            .setNegativeButton("Keep Order", null)
            .setPositiveButton("Cancel Order") { _, _ ->
                cancelOrder()
            }
            .show()
    }

    // Update the order status safely using a Firestore transaction
    private fun cancelOrder() {

        val user = auth.currentUser

        if (user == null) {
            goToLogin()
            return
        }

        val orderId = currentOrderId

        if (orderId.isNullOrBlank()) {
            Toast.makeText(
                this,
                "Order ID is missing.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (cancellationInProgress) {
            return
        }

        cancellationInProgress = true
        updateCancelButton(currentOrderStatus)

        val orderReference = db.collection("orders")
            .document(orderId)

        db.runTransaction { transaction ->

            val document = transaction.get(orderReference)

            if (!document.exists()) {
                throw IllegalStateException("Order not found.")
            }

            val orderUserId = document.getString("userId")

            if (orderUserId != user.uid) {
                throw SecurityException(
                    "You cannot cancel this order."
                )
            }

            val latestStatus =
                document.getString("status") ?: "Unknown"

            if (!isCancellableStatus(latestStatus)) {
                throw IllegalStateException(
                    "This order can no longer be cancelled."
                )
            }

            transaction.update(
                orderReference,
                "status",
                "Cancelled"
            )

            null
        }.addOnSuccessListener {

            cancellationInProgress = false
            currentOrderStatus = "Cancelled"

            // Update the screen immediately
            tvDetailStatus.text = "Status: Cancelled"

            updateTracking(currentOrderStatus)
            updateCancelButton(currentOrderStatus)

            Toast.makeText(
                this,
                "Order cancelled successfully.",
                Toast.LENGTH_LONG
            ).show()
        }.addOnFailureListener { error ->

            cancellationInProgress = false
            updateCancelButton(currentOrderStatus)

            Toast.makeText(
                this,
                error.localizedMessage
                    ?: "Could not cancel the order. Please try again.",
                Toast.LENGTH_LONG
            ).show()

            // Reload the latest status in case it changed elsewhere
            loadOrderDetails()
        }
    }

    // Update the order tracking timeline
    private fun updateTracking(status: String) {

        val normalizedStatus = status.trim().lowercase()

        val isCancelled =
            normalizedStatus == "cancelled" ||
                    normalizedStatus == "canceled"

        if (isCancelled) {

            trackingCard.visibility = View.GONE
            tvCancelledMessage.visibility = View.VISIBLE

            return
        }

        trackingCard.visibility = View.VISIBLE
        tvCancelledMessage.visibility = View.GONE

        val stage = when (normalizedStatus) {
            "order placed", "placed", "pending" -> 0
            "preparing", "confirmed" -> 1
            "out for delivery", "on the way", "shipped" -> 2
            "delivered", "completed" -> 3
            else -> 1
        }

        updateStage(
            iconId = R.id.tvPlacedIcon,
            titleId = R.id.tvPlacedTitle,
            completed = stage >= 0,
            current = stage == 0,
            number = "1"
        )

        updateStage(
            iconId = R.id.tvPreparingIcon,
            titleId = R.id.tvPreparingTitle,
            completed = stage >= 1,
            current = stage == 1,
            number = "2"
        )

        updateStage(
            iconId = R.id.tvDeliveryIcon,
            titleId = R.id.tvDeliveryTitle,
            completed = stage >= 2,
            current = stage == 2,
            number = "3"
        )

        updateStage(
            iconId = R.id.tvDeliveredIcon,
            titleId = R.id.tvDeliveredTitle,
            completed = stage >= 3,
            current = stage == 3,
            number = "4"
        )

        findViewById<View>(R.id.linePlaced)
            .setBackgroundColor(
                if (stage >= 1) orange else Color.rgb(229, 220, 214)
            )

        findViewById<View>(R.id.linePreparing)
            .setBackgroundColor(
                if (stage >= 2) orange else Color.rgb(229, 220, 214)
            )

        findViewById<View>(R.id.lineDelivery)
            .setBackgroundColor(
                if (stage >= 3) green else Color.rgb(229, 220, 214)
            )
    }

    // Update individual tracking stages
    private fun updateStage(
        iconId: Int,
        titleId: Int,
        completed: Boolean,
        current: Boolean,
        number: String
    ) {

        val icon = findViewById<TextView>(iconId)
        val title = findViewById<TextView>(titleId)

        val isDelivered = current && number == "4"

        icon.text = if (completed) "✓" else number

        icon.setTextColor(
            when {
                isDelivered -> green
                current -> orange
                completed -> green
                else -> grey
            }
        )

        icon.setBackgroundColor(
            when {
                isDelivered -> paleGreen
                current -> paleOrange
                completed -> paleGreen
                else -> paleGrey
            }
        )

        title.setTextColor(
            when {
                isDelivered -> green
                current -> orange
                completed -> green
                else -> grey
            }
        )
    }

    // Return to login if the user is not authenticated
    private fun goToLogin() {

        val intent = Intent(this, LoginActivity::class.java)

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }
}
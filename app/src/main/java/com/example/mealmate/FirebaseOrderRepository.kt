package com.example.mealmate

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseOrderRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun saveOrder(
        order: Order,
        address: String,
        phone: String,
        paymentMethod: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {

        val user = auth.currentUser

        if (user == null) {
            onFailure("Please log in before placing an order.")
            return
        }

        val orderData = hashMapOf(
            "orderId" to order.orderId,
            "userId" to user.uid,
            "foodItems" to order.foodItems,
            "date" to order.date,
            "amount" to order.amount,
            "status" to order.status,
            "address" to address,
            "phone" to phone,
            "paymentMethod" to paymentMethod
        )

        db.collection("orders")
            .document(order.orderId)
            .set(orderData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { error ->
                onFailure(
                    error.localizedMessage
                        ?: "Could not save order."
                )
            }
    }
}
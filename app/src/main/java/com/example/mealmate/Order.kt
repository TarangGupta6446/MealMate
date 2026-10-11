package com.example.mealmate

data class Order(
    val orderId: String,
    val foodItems: String,
    val date: String,
    val amount: Int,
    val status: String
)
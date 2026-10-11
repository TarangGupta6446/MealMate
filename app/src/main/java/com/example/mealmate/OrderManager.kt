package com.example.mealmate

object OrderManager {

    val currentOrders = mutableListOf<Order>()

    val previousOrders = mutableListOf<Order>()

    fun addOrder(order: Order) {
        currentOrders.add(0, order)
    }
}
package com.example.mealmate

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object CartManager {

    val cartItems = mutableListOf<CartItem>()

    private const val PREFS_NAME = "mealmate_cart"
    private const val CART_KEY = "cart_items"

    private var initialized = false
    private var appContext: Context? = null

    /**
     * Initialize the cart and restore previously saved items.
     * Call this once when the application starts.
     */
    fun initialize(context: Context) {
        if (initialized) return

        appContext = context.applicationContext
        loadCart()
        initialized = true
    }

    fun addItem(item: CartItem) {
        val existingIndex = cartItems.indexOfFirst {
            it.name == item.name
        }

        if (existingIndex != -1) {
            val existingItem = cartItems[existingIndex]

            cartItems[existingIndex] = existingItem.copy(
                quantity = existingItem.quantity + item.quantity
            )
        } else {
            cartItems.add(item)
        }

        saveCart()
    }

    fun removeItem(position: Int) {
        if (position in cartItems.indices) {
            cartItems.removeAt(position)
            saveCart()
        }
    }

    fun updateQuantity(position: Int, quantity: Int) {
        if (position !in cartItems.indices) return

        if (quantity <= 0) {
            removeItem(position)
            return
        }

        cartItems[position] = cartItems[position].copy(
            quantity = quantity
        )

        saveCart()
    }

    fun clearCart() {
        cartItems.clear()
        saveCart()
    }

    fun getTotal(): Int {
        return cartItems.sumOf {
            it.price * it.quantity
        }
    }

    private fun saveCart() {
        val context = appContext ?: return
        val jsonArray = JSONArray()

        for (item in cartItems) {
            val jsonObject = JSONObject().apply {
                put("name", item.name)
                put("price", item.price)
                put("quantity", item.quantity)
                put("image", item.image)
            }

            jsonArray.put(jsonObject)
        }

        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        ).edit()
            .putString(CART_KEY, jsonArray.toString())
            .apply()
    }

    private fun loadCart() {
        val context = appContext ?: return

        val savedCart = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        ).getString(CART_KEY, null) ?: return

        try {
            val jsonArray = JSONArray(savedCart)
            cartItems.clear()

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)

                cartItems.add(
                    CartItem(
                        name = item.getString("name"),
                        price = item.getInt("price"),
                        quantity = item.getInt("quantity"),
                        image = item.getInt("image")
                    )
                )
            }
        } catch (e: Exception) {
            cartItems.clear()
        }
    }
}
package com.example.mealmate

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

abstract class BaseNavigationActivity : AppCompatActivity() {

    protected fun setupBottomNavigation(
        selectedTab: String
    ) {
        val navHome = findViewById<TextView>(R.id.navHome)
        val navCart = findViewById<TextView>(R.id.navCart)
        val navOrders = findViewById<TextView>(R.id.navOrders)
        val navProfile = findViewById<TextView>(R.id.navProfile)

        val orange = Color.rgb(255, 107, 53)
        val grey = Color.rgb(119, 119, 119)

        navHome.setTextColor(if (selectedTab == "home") orange else grey)
        navCart.setTextColor(if (selectedTab == "cart") orange else grey)
        navOrders.setTextColor(if (selectedTab == "orders") orange else grey)
        navProfile.setTextColor(if (selectedTab == "profile") orange else grey)

        navHome.setOnClickListener {
            navigateTo(HomeActivity::class.java, selectedTab, "home")
        }

        navCart.setOnClickListener {
            navigateTo(CartActivity::class.java, selectedTab, "cart")
        }

        navOrders.setOnClickListener {
            navigateTo(OrdersActivity::class.java, selectedTab, "orders")
        }

        navProfile.setOnClickListener {
            navigateTo(ProfileActivity::class.java, selectedTab, "profile")
        }
    }

    private fun navigateTo(
        destination: Class<*>,
        currentTab: String,
        targetTab: String
    ) {
        if (currentTab == targetTab) {
            return
        }

        val intent = Intent(this, destination)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
    }
}
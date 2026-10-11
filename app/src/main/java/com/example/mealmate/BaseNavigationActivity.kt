package com.example.mealmate

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

abstract class BaseNavigationActivity : AppCompatActivity() {

    protected fun setupBottomNavigation(selectedTab: String) {

        val navHome = findViewById<View>(R.id.navHome)
        val navCart = findViewById<View>(R.id.navCart)
        val navOrders = findViewById<View>(R.id.navOrders)
        val navProfile = findViewById<View>(R.id.navProfile)

        val orange = Color.rgb(255, 107, 53)
        val grey = Color.rgb(130, 119, 115)

        updateNavigationItem(
            R.id.navHomeIcon,
            R.id.navHomeLabel,
            selectedTab == "home",
            orange,
            grey
        )

        updateNavigationItem(
            R.id.navCartIcon,
            R.id.navCartLabel,
            selectedTab == "cart",
            orange,
            grey
        )

        updateNavigationItem(
            R.id.navOrdersIcon,
            R.id.navOrdersLabel,
            selectedTab == "orders",
            orange,
            grey
        )

        updateNavigationItem(
            R.id.navProfileIcon,
            R.id.navProfileLabel,
            selectedTab == "profile",
            orange,
            grey
        )

        navHome?.setOnClickListener {
            navigateTo(HomeActivity::class.java, selectedTab, "home")
        }

        navCart?.setOnClickListener {
            navigateTo(CartActivity::class.java, selectedTab, "cart")
        }

        navOrders?.setOnClickListener {
            navigateTo(OrdersActivity::class.java, selectedTab, "orders")
        }

        navProfile?.setOnClickListener {
            navigateTo(ProfileActivity::class.java, selectedTab, "profile")
        }
    }

    private fun updateNavigationItem(
        iconId: Int,
        labelId: Int,
        selected: Boolean,
        orange: Int,
        grey: Int
    ) {

        val color = if (selected) orange else grey

        val icon = findViewById<ImageView>(iconId)
        val label = findViewById<TextView>(labelId)

        // Prevent a missing view from crashing the application.
        icon?.setColorFilter(color)
        label?.setTextColor(color)
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
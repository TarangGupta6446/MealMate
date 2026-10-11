package com.example.mealmate

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FoodDetailsActivity : AppCompatActivity() {

    private var quantity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersiveMode.enable(this)

        setContentView(R.layout.activity_food_details)

        val foodName = intent.getStringExtra("food_name")
        val foodDescription = intent.getStringExtra("food_description")
        val foodPrice = intent.getIntExtra("food_price", 0)
        val foodImage = intent.getIntExtra(
            "food_image",
            android.R.drawable.ic_menu_gallery
        )

        val ivFood = findViewById<ImageView>(R.id.ivFoodDetails)
        val tvName = findViewById<TextView>(R.id.tvDetailsName)
        val tvDescription = findViewById<TextView>(R.id.tvDescription)
        val tvPrice = findViewById<TextView>(R.id.tvDetailsPrice)

        val tvMinus = findViewById<TextView>(R.id.tvMinus)
        val tvPlus = findViewById<TextView>(R.id.tvPlus)
        val tvQuantity = findViewById<TextView>(R.id.tvQuantity)

        val btnAddToCart = findViewById<Button>(R.id.btnAddToCart)
        val tvBack = findViewById<TextView>(R.id.tvBack)

        // Display food information
        tvName.text = foodName
        tvDescription.text = foodDescription
        tvPrice.text = "₹$foodPrice"
        ivFood.setImageResource(foodImage)

        // Increase quantity
        tvPlus.setOnClickListener {

            quantity++

            tvQuantity.text = quantity.toString()
        }

        // Decrease quantity
        tvMinus.setOnClickListener {

            if (quantity > 1) {
                quantity--
                tvQuantity.text = quantity.toString()
            }
        }

        // Back button
        tvBack.setOnClickListener {
            finish()
        }

        // Add to cart
        btnAddToCart.setOnClickListener {

            val cartItem = CartItem(
                name = foodName ?: "Food",
                price = foodPrice,
                quantity = quantity,
                image = foodImage
            )

            CartManager.addItem(cartItem)

            finish()
        }
    }
}
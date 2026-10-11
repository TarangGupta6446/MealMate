package com.example.mealmate

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : BaseNavigationActivity() {

    private lateinit var rvFood: RecyclerView
    private lateinit var rvCategories: RecyclerView
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var etSearch: EditText
    private lateinit var tvPopular: TextView
    private lateinit var tvSeeAll: TextView

    private val foodRepository = FoodRepository()

    private var allFoods: List<Food> = emptyList()
    private var selectedCategory = "All"
    private var searchQuery = ""

    private val categories = listOf(
        "All",
        "Pizza",
        "Burgers",
        "Biryani",
        "Pasta",
        "Desserts",
        "Drinks"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ImmersiveMode.enable(this)
        setContentView(R.layout.activity_home)

        rvFood = findViewById(R.id.rvFood)
        rvCategories = findViewById(R.id.rvCategories)
        etSearch = findViewById(R.id.etSearch)
        tvPopular = findViewById(R.id.tvPopular)
        tvSeeAll = findViewById(R.id.tvSeeAll)

        setupFoodRecyclerView()
        setupCategories()
        setupBottomNavigation("home")
        setupNavigation()
        setupSearch()
        setupSeeAll()
        loadFoods()
    }

    private fun setupFoodRecyclerView() {
        rvFood.layoutManager = GridLayoutManager(this, 2)

        foodAdapter = FoodAdapter(emptyList()) { food ->
            openFoodDetails(food)
        }

        rvFood.adapter = foodAdapter
    }

    private fun setupCategories() {
        rvCategories.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        categoryAdapter = CategoryAdapter(categories) { category ->
            selectedCategory = category
            updateFoodList()
        }

        rvCategories.adapter = categoryAdapter
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                searchQuery = s?.toString()?.trim().orEmpty()
                updateFoodList()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun setupSeeAll() {
        tvSeeAll.setOnClickListener {
            selectedCategory = "All"

            if (etSearch.text.isNotEmpty()) {
                etSearch.text.clear()
            } else {
                searchQuery = ""
                updateFoodList()
            }

            rvCategories.smoothScrollToPosition(0)
        }
    }

    private fun loadFoods() {
        foodRepository.getFoods(
            onSuccess = { foodList ->
                allFoods = foodList
                updateFoodList()

                if (allFoods.isEmpty()) {
                    Toast.makeText(
                        this,
                        "No food items found in the menu.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            onFailure = { error ->
                Toast.makeText(
                    this,
                    "Could not load menu: $error",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun updateFoodList() {
        val categoryFilteredFoods = when (selectedCategory) {

            "All" -> allFoods

            "Pizza" -> allFoods.filter {
                it.name.contains("pizza", ignoreCase = true)
            }

            "Burgers" -> allFoods.filter {
                it.name.contains("burger", ignoreCase = true)
            }

            "Biryani" -> allFoods.filter {
                it.name.contains("biryani", ignoreCase = true)
            }

            "Pasta" -> allFoods.filter {
                it.name.contains("pasta", ignoreCase = true)
            }

            "Desserts" -> allFoods.filter {
                it.name.contains("dessert", ignoreCase = true) ||
                        it.name.contains("cake", ignoreCase = true) ||
                        it.name.contains("ice cream", ignoreCase = true) ||
                        it.name.contains("brownie", ignoreCase = true)
            }

            "Drinks" -> allFoods.filter {
                it.name.contains("drink", ignoreCase = true) ||
                        it.name.contains("juice", ignoreCase = true) ||
                        it.name.contains("coffee", ignoreCase = true) ||
                        it.name.contains("shake", ignoreCase = true) ||
                        it.name.contains("cola", ignoreCase = true) ||
                        it.name.contains("lemonade", ignoreCase = true)
            }

            else -> allFoods
        }

        val filteredFoods = if (searchQuery.isBlank()) {
            categoryFilteredFoods
        } else {
            categoryFilteredFoods.filter { food ->
                food.name.contains(searchQuery, ignoreCase = true) ||
                        food.description.contains(searchQuery, ignoreCase = true)
            }
        }

        foodAdapter = FoodAdapter(filteredFoods) { food ->
            openFoodDetails(food)
        }

        rvFood.adapter = foodAdapter

        tvPopular.text = when {
            searchQuery.isNotBlank() -> "Search Results"
            selectedCategory != "All" -> selectedCategory
            else -> "Popular Food"
        }

        if (filteredFoods.isEmpty() &&
            (searchQuery.isNotBlank() || selectedCategory != "All")
        ) {
            Toast.makeText(
                this,
                "No matching food items found.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun openFoodDetails(food: Food) {
        val intent = Intent(this, FoodDetailsActivity::class.java)

        intent.putExtra("food_name", food.name)
        intent.putExtra("food_description", food.description)
        intent.putExtra("food_price", food.price)
        intent.putExtra("food_image", food.image)

        startActivity(intent)
    }

    private fun setupNavigation() {
        findViewById<TextView>(R.id.navCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        findViewById<TextView>(R.id.navHome).setOnClickListener {
            // Already on Home
        }

        findViewById<TextView>(R.id.navOrders).setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }

        findViewById<TextView>(R.id.navProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
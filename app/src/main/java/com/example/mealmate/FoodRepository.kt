package com.example.mealmate

import com.google.firebase.firestore.FirebaseFirestore

class FoodRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getFoods(
        onSuccess: (List<Food>) -> Unit,
        onFailure: (String) -> Unit
    ) {

        db.collection("foods")
            .get()
            .addOnSuccessListener { documents ->

                val foodList = mutableListOf<Food>()

                for (document in documents) {

                    val name =
                        document.getString("name") ?: continue

                    val description =
                        document.getString("description") ?: ""

                    val price =
                        (document.getLong("price") ?: 0L).toInt()

                    // Select the correct local image for each food.
                    val image = when (document.id) {

                        "pizza1" ->
                            R.drawable.pizza

                        "burger1" ->
                            R.drawable.burger

                        "biryani1" ->
                            R.drawable.biryani

                        "pasta1" ->
                            R.drawable.pasta

                        else ->
                            android.R.drawable.ic_menu_gallery
                    }

                    val food = Food(
                        name = name,
                        description = description,
                        price = price,
                        image = image
                    )

                    foodList.add(food)
                }

                onSuccess(foodList)
            }
            .addOnFailureListener { error ->

                onFailure(
                    error.localizedMessage
                        ?: "Unable to load food menu."
                )
            }
    }
}
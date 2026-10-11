package com.example.mealmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter(
    private val foodList: List<Food>,
    private val onFoodClick: (Food) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val foodImage: ImageView = itemView.findViewById(R.id.ivFood)
        val foodName: TextView = itemView.findViewById(R.id.tvFoodName)
        val foodDescription: TextView =
            itemView.findViewById(R.id.tvFoodDescription)
        val foodPrice: TextView = itemView.findViewById(R.id.tvFoodPrice)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FoodViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)

        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: FoodViewHolder,
        position: Int
    ) {

        val food = foodList[position]

        holder.foodName.text = food.name
        holder.foodDescription.text = food.description
        holder.foodPrice.text = "₹${food.price}"
        holder.foodImage.setImageResource(food.image)

        holder.itemView.setOnClickListener {
            onFoodClick(food)
        }
    }

    override fun getItemCount(): Int {
        return foodList.size
    }
}
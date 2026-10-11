package com.example.mealmate

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val categories: List<String>,
    private val onCategoryClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedCategory = "All"

    private val orange = Color.rgb(255, 75, 32)
    private val darkText = Color.rgb(45, 40, 37)
    private val borderColor = Color.rgb(255, 224, 211)

    inner class CategoryViewHolder(
        val container: LinearLayout,
        val title: TextView
    ) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {

        val container = LinearLayout(parent.context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(
                dp(parent, 16),
                dp(parent, 10),
                dp(parent, 16),
                dp(parent, 10)
            )

            isClickable = true
            isFocusable = true
        }

        val title = TextView(parent.context).apply {
            textSize = 14f
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
            maxLines = 1
        }

        container.addView(
            title,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        val params = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            dp(parent, 48)
        )

        params.marginEnd = dp(parent, 10)
        container.layoutParams = params

        return CategoryViewHolder(container, title)
    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int
    ) {

        val category = categories[position]
        val isSelected = category == selectedCategory

        holder.title.text = category

        val background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(holder.container, 24).toFloat()

            setColor(
                if (isSelected) orange else Color.WHITE
            )

            if (!isSelected) {
                setStroke(dp(holder.container, 1), borderColor)
            }
        }

        holder.container.background = background

        holder.title.setTextColor(
            if (isSelected) Color.WHITE else darkText
        )

        holder.container.setOnClickListener {
            val previousPosition = categories.indexOf(selectedCategory)

            if (selectedCategory == category) return@setOnClickListener

            selectedCategory = category

            if (previousPosition >= 0) {
                notifyItemChanged(previousPosition)
            }

            notifyItemChanged(holder.bindingAdapterPosition)

            onCategoryClick(category)
        }
    }

    override fun getItemCount(): Int = categories.size

    private fun dp(
        view: android.view.View,
        value: Int
    ): Int {
        return (value * view.resources.displayMetrics.density).toInt()
    }
}
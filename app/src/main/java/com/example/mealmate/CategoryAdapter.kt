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

    inner class CategoryViewHolder(
        val container: LinearLayout,
        val title: TextView
    ) : RecyclerView.ViewHolder(container)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {

        val container = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(18, 12, 18, 12)
        }

        val title = TextView(parent.context).apply {
            textSize = 14f
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
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
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(0, 0, 10, 0)
        container.layoutParams = params

        return CategoryViewHolder(container, title)
    }

    override fun onBindViewHolder(
        holder: CategoryViewHolder,
        position: Int
    ) {

        val category = categories[position]
        holder.title.text = category

        val isSelected = category == selectedCategory

        val background = GradientDrawable().apply {
            cornerRadius = 40f

            setColor(
                if (isSelected) {
                    Color.rgb(255, 107, 53)
                } else {
                    Color.WHITE
                }
            )

            if (!isSelected) {
                setStroke(2, Color.rgb(255, 224, 211))
            }
        }

        holder.container.background = background

        holder.title.setTextColor(
            if (isSelected) {
                Color.WHITE
            } else {
                Color.rgb(65, 55, 50)
            }
        )

        holder.container.setOnClickListener {

            val previousCategory = selectedCategory
            selectedCategory = category

            val previousPosition = categories.indexOf(previousCategory)

            if (previousPosition >= 0) {
                notifyItemChanged(previousPosition)
            }

            notifyItemChanged(position)

            onCategoryClick(category)
        }
    }

    override fun getItemCount(): Int = categories.size
}
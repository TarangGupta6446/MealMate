package com.example.mealmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onRemove: (Int) -> Unit,
    private val onQuantityChanged: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val image: ImageView = itemView.findViewById(R.id.ivCartFood)
        val name: TextView = itemView.findViewById(R.id.tvCartFoodName)
        val price: TextView = itemView.findViewById(R.id.tvCartFoodPrice)
        val quantity: TextView = itemView.findViewById(R.id.tvCartQuantity)
        val minus: TextView = itemView.findViewById(R.id.tvCartMinus)
        val plus: TextView = itemView.findViewById(R.id.tvCartPlus)
        val remove: TextView = itemView.findViewById(R.id.tvRemove)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)

        return CartViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {
        val item = cartItems[position]

        holder.image.setImageResource(item.image)
        holder.name.text = item.name
        holder.price.text = "₹${item.price}"
        holder.quantity.text = item.quantity.toString()

        holder.minus.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition

            if (currentPosition != RecyclerView.NO_POSITION) {
                val currentItem = cartItems[currentPosition]

                if (currentItem.quantity > 1) {
                    CartManager.updateQuantity(
                        currentPosition,
                        currentItem.quantity - 1
                    )

                    notifyItemChanged(currentPosition)
                    onQuantityChanged()
                } else {
                    onRemove(currentPosition)
                }
            }
        }

        holder.plus.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition

            if (currentPosition != RecyclerView.NO_POSITION) {
                val currentItem = cartItems[currentPosition]

                CartManager.updateQuantity(
                    currentPosition,
                    currentItem.quantity + 1
                )

                notifyItemChanged(currentPosition)
                onQuantityChanged()
            }
        }

        holder.remove.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition

            if (currentPosition != RecyclerView.NO_POSITION) {
                onRemove(currentPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}
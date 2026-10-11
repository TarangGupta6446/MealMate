package com.example.mealmate

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(
    private val orderList: List<Order>
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val orderId: TextView =
            itemView.findViewById(R.id.tvOrderItemId)

        val status: TextView =
            itemView.findViewById(R.id.tvOrderStatus)

        val food: TextView =
            itemView.findViewById(R.id.tvOrderFood)

        val date: TextView =
            itemView.findViewById(R.id.tvOrderDate)

        val amount: TextView =
            itemView.findViewById(R.id.tvOrderAmount)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)

        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: OrderViewHolder,
        position: Int
    ) {

        val order = orderList[position]

        holder.orderId.text = order.orderId
        holder.status.text = order.status
        holder.food.text = order.foodItems
        holder.date.text = order.date
        holder.amount.text = "₹${order.amount}"

        // Make the order card clickable
        holder.itemView.setOnClickListener {

            val context = holder.itemView.context

            val intent = Intent(
                context,
                OrderDetailsActivity::class.java
            )

            intent.putExtra("order_id", order.orderId)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}
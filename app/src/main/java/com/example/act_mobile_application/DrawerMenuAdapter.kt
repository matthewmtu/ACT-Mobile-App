package com.example.act_mobile_application

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DrawerMenuAdapter(private var items: List<MenuItem>, private val onItemClick: (MenuItem) -> Unit) :
    RecyclerView.Adapter<DrawerMenuAdapter.ViewHolder>() {

    // ViewHolder for the menu item
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = items.size

    // Update the menu items
    fun updateMenuItems(newItems: List<MenuItem>) {
        items = newItems
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}


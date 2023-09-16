package com.example.torndashboard.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.torndashboard.R
import com.example.torndashboard.utils.Item

class TextAdapter(private val itemsList: MutableList<Item>, private val onItemClick: () -> Unit) :
    RecyclerView.Adapter<TextAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemTypeTextView: TextView = view.findViewById(R.id.itemTypeTextView)
        val textTextView: TextView = view.findViewById(R.id.textTextView)
        val timeTextView: TextView = view.findViewById(R.id.timeTextView)

        init {
            itemView.setOnClickListener {
                onItemClick()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemsList[position]

        holder.itemTypeTextView.text = item.itemType
        if (item.itemType == "Error") {
            holder.itemTypeTextView.setTextColor(Color.RED)
            holder.textTextView.setTextColor(Color.RED)
            holder.timeTextView.setTextColor(Color.RED)
        }

        holder.textTextView.text = item.text

        holder.timeTextView.text = item.time
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}

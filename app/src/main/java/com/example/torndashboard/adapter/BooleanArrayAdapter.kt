package com.example.torndashboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.torndashboard.R
import com.example.torndashboard.config.AppConfig.timeMinText

class BooleanArrayAdapter(
    private var timeFilter: BooleanArray,
    private var timeIsZeroTextVisibility : BooleanArray
) : RecyclerView.Adapter<BooleanArrayAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textView)
        private val timeFilterButton: RadioButton = itemView.findViewById(R.id.timeFilterButton)
        private val timeIsZeroButton: RadioButton = itemView.findViewById(R.id.timeIsZeroButton)

        fun bind(position: Int) {
            textView.text = timeMinText[position]
            timeFilterButton.isChecked = timeFilter[position]
            timeIsZeroButton.isChecked = timeIsZeroTextVisibility[position]

            timeFilterButton.setOnCheckedChangeListener { _, isChecked ->
                timeFilter[position] = isChecked
            }

            timeIsZeroButton.setOnCheckedChangeListener { _, isChecked ->
                timeIsZeroTextVisibility[position] = isChecked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_boolean_array, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return timeFilter.size
    }
}

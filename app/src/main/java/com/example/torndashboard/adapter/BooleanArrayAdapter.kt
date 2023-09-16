package com.example.torndashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.torndashboard.R
import com.example.torndashboard.config.AppConfig.timeMinText
import com.example.torndashboard.preferences.TimeBooleanArrayPreferences
import com.google.android.material.switchmaterial.SwitchMaterial

class BooleanArrayAdapter(
    private var timeFilter: BooleanArray,
    private var timeIsZeroTextVisibility : BooleanArray,
    var context: Context
) : RecyclerView.Adapter<BooleanArrayAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textView)
        private val timeFilterSwitch: SwitchMaterial = itemView.findViewById(R.id.timeFilterSwitch)
        private val timeIsZeroSwitch: SwitchMaterial = itemView.findViewById(R.id.timeIsZeroSwitch)

        fun bind(position: Int) {
            textView.text = timeMinText[position]
            timeFilterSwitch.isChecked = timeFilter[position]
            timeIsZeroSwitch.isChecked = timeIsZeroTextVisibility[position]

            val timeBooleanArrayPreferences = TimeBooleanArrayPreferences(context)

            timeFilterSwitch.setOnCheckedChangeListener { _, isChecked ->
                timeFilter[position] = isChecked

                timeBooleanArrayPreferences.saveTimeFilter(timeFilter)
            }

            timeIsZeroSwitch.setOnCheckedChangeListener { _, isChecked ->
                timeIsZeroTextVisibility[position] = isChecked

                timeBooleanArrayPreferences.saveTimeIsZeroTextVisibility(timeIsZeroTextVisibility)
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

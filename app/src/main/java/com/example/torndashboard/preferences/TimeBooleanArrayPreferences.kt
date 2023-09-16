package com.example.torndashboard.preferences

import android.content.Context
import android.content.SharedPreferences

class TimeBooleanArrayPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("TimeBooleanArrayPreferences", Context.MODE_PRIVATE)

    fun saveTimeFilter(timeFilter: BooleanArray) {
        val editor = sharedPreferences.edit()
        editor.putString("timeFilter", timeFilter.joinToString(","))
        editor.apply()
    }

    fun getTimeFilter(): BooleanArray {
        val savedTimeFilter = sharedPreferences.getString("timeFilter", null)

        var t =  savedTimeFilter?.split(",")?.map { it.toBoolean() }?.toBooleanArray()

        if (t == null){
            t = booleanArrayOf(true, true, false, false, true, true, false, true)
            saveTimeFilter(t)
        }

        return t
    }

    fun saveTimeIsZeroTextVisibility(timeIsZeroTextVisibility: BooleanArray) {
        val editor = sharedPreferences.edit()
        editor.putString("timeIsZeroTextVisibility", timeIsZeroTextVisibility.joinToString(","))
        editor.apply()
    }

    fun getTimeIsZeroTextVisibility(): BooleanArray {
        val savedVisibility = sharedPreferences.getString("timeIsZeroTextVisibility", null)
        var t = savedVisibility?.split(",")?.map { it.toBoolean() }?.toBooleanArray()

        if (t==null){
            t=booleanArrayOf(true, true, false, false, true, true, false, false)
            saveTimeIsZeroTextVisibility(t)
        }

        return t
    }
}

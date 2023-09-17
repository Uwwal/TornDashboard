package com.example.torndashboard.preferences

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.torndashboard.config.AppConfig.maxTime

class WidgetProviderSharedPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("WidgetProviderSharedPreferences", Context.MODE_PRIVATE)
    private val minTimeKey = "minTime"
    private val lastUpdateTimeKey = "lastUpdateTime"

    fun saveMinTime(minTime : Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(minTimeKey, minTime)
        editor.apply()
    }

    fun getMinTime(): Int {
        return sharedPreferences.getInt(minTimeKey, maxTime)
    }

    fun saveLastUpdateTime(lastUpdateTime: Long) {
        val editor = sharedPreferences.edit()
        editor.putLong(lastUpdateTimeKey, lastUpdateTime)
        editor.apply()
    }

    fun getLastUpdateTime(): Long {
        return sharedPreferences.getLong(lastUpdateTimeKey, 0)
    }
}
package com.example.torndashboard.config

import android.content.Context
import android.view.View
import com.example.torndashboard.preferences.TimeBooleanArrayPreferences
import com.example.torndashboard.utils.FileUtils
import com.example.torndashboard.web.RetrofitClient
import java.io.File

object AppConfig {
    const val configFileName = "config.json"

    var textViewCheckRemindVisibility = View.GONE

    const val maxTime = 864000

    var timeFilter : BooleanArray =  booleanArrayOf(true, true, false, false, true, true, false, true)
    var timeMinText : Array<String> =  arrayOf("能量", "神经", "快乐", "生命", "毒品", "饮料", "医疗", "飞行")
    var timeIsZeroTextVisibility : BooleanArray = booleanArrayOf(true, true, false, false, true, true, false, false)

    fun initialize(context: Context) {
        val timeBooleanArrayPreferences = TimeBooleanArrayPreferences(context)

        timeFilter = timeBooleanArrayPreferences.getTimeFilter()
        timeIsZeroTextVisibility = timeBooleanArrayPreferences.getTimeIsZeroTextVisibility()
    }
}
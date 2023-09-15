package com.example.torndashboard.config

import android.view.View

object AppConfig {
    const val configFileName = "config.json"
    var textViewCheckRemindVisibility = View.GONE
    const val maxTime = 864000

    var timeFilter : BooleanArray =  booleanArrayOf(true, true, false, false, true, true, false, true)

    fun initialize() {

    }
}
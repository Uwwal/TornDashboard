package com.example.torndashboard.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getMinTimeHHMMFormatted(lastUpdateTime: Long, minTime: Int): String {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val newTime = Date(lastUpdateTime + (minTime * 1000L))

    return dateFormat.format(newTime)
}


fun getCurrentTimeFormatted(): String {
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val currentTime = Date()
    return dateFormat.format(currentTime)
}
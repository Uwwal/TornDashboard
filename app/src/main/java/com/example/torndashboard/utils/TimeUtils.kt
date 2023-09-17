package com.example.torndashboard.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getMinTimeHHMMFormatted(minTime: Int): String {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val currentTime = Date()
    val newTime = Date(currentTime.time + (minTime * 1000L))

    return dateFormat.format(newTime)
}


fun getCurrentTimeFormatted(): String {
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val currentTime = Date()
    return dateFormat.format(currentTime)
}
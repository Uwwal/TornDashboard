package com.example.torndashboard.utils

import android.content.Context
import com.example.torndashboard.config.AppConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class FileUtils(context: Context) {
    private val gson = Gson()
    private val configFile = File(context.filesDir, AppConfig.configFileName)

    // configFile.writeText(gson.toJson(configMap))

    val key = "Key"
    val minAutoSetClockSwitchStatus = "MinAutoSetClock"

    fun loadConfigMapFromFile(): MutableMap<String, String> {
        if (configFile.exists()) {
            val json = configFile.readText()
            return gson.fromJson(
                json,
                object : TypeToken<MutableMap<String, String>>() {}.type
            )
        }
        return mutableMapOf()
    }

    fun get(key: String): String? {
        if (configFile.exists()) {
            val map = loadConfigMapFromFile()

            return map[key]
        }
        return null
    }

    fun write(string: String) {
        configFile.writeText(string)
    }
}

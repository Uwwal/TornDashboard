package com.example.torndashboard.utils

import android.content.Context
import com.example.torndashboard.config.AppConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class FileUtils(private val context: Context) {

    private val gson = Gson()

    private fun loadConfigMapFromFile(configFile: File): Map<String, String> {
        if (configFile.exists()) {
            val json = configFile.readText()
            return gson.fromJson(
                json,
                object : TypeToken<Map<String, String>>() {}.type
            )
        }
        return emptyMap()
    }

    fun getKey(): String? {
        val configFile = File(context.filesDir, AppConfig.configFileName)
        val map = loadConfigMapFromFile(configFile)

        return map["KEY"]
    }

}

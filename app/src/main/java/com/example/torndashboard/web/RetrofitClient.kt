package com.example.torndashboard.web

import android.content.Context
import com.example.torndashboard.config.AppConfig
import com.example.torndashboard.utils.FileUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

object RetrofitClient {
    private const val BASE_URL = "https://api.torn.com/"

    private var API_KEY = ""

    fun checkApiKey(context: Context){
        if(API_KEY.isEmpty()){
            initializeApiKey(context)
        }
    }

    private fun initializeApiKey(context: Context) {
        val configFile = File(context.filesDir, AppConfig.configFileName)
        if (configFile.exists()) {
            val fileUtils = FileUtils(context)
            val key = fileUtils.get(fileUtils.key)

            if (!key.isNullOrEmpty()) {
                API_KEY = key
            }
        }
    }

    fun setApiKey(key:String){
        API_KEY = key
    }

    val apiService: TornApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiKeyInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val modifiedUrl = originalRequest.url().newBuilder()
                .addQueryParameter("key", API_KEY)
                .build()
            val modifiedRequest = originalRequest.newBuilder()
                .url(modifiedUrl)
                .build()
            chain.proceed(modifiedRequest)
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .build()

        retrofit.newBuilder()
            .client(okHttpClient)
            .build()
            .create(TornApiService::class.java)
    }
}

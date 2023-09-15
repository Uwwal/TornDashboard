package com.example.torndashboard.web

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.torn.com/"

    private var API_KEY = ""

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

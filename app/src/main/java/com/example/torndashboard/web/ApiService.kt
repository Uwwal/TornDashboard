package com.example.torndashboard.web

import com.example.torndashboard.utils.CooldownsResponse
import com.example.torndashboard.utils.MoneyResponse
import com.example.torndashboard.utils.StatsResponse
import retrofit2.Call
import retrofit2.http.GET

interface TornApiService {
    @GET("user/?selections=money")
    fun getMoneyInfo(): Call<MoneyResponse>

    @GET("user/?selections=bars")
    fun getStatsInfo(): Call<StatsResponse>

    @GET("/user/?selections=cooldowns")
    fun getCooldownsInfo(): Call<CooldownsResponse>
}
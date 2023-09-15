package com.example.torndashboard.web

import com.example.torndashboard.utils.CooldownsResponse
import com.example.torndashboard.utils.EventsResponse
import com.example.torndashboard.utils.MoneyResponse
import com.example.torndashboard.utils.StatsResponse
import com.example.torndashboard.utils.TravelResponse
import retrofit2.Call
import retrofit2.http.GET

interface TornApiService {
    @GET("user/?selections=money")
    fun getMoneyInfo(): Call<MoneyResponse>

    @GET("user/?selections=bars")
    fun getStatsInfo(): Call<StatsResponse>

    @GET("/user/?selections=cooldowns")
    fun getCooldownsInfo(): Call<CooldownsResponse>

    @GET("/user/?selections=travel")
    fun getTravelInfo(): Call<TravelResponse>

    @GET("/user/?selections=newevents")
    fun getEventsInfo(): Call<EventsResponse>
}
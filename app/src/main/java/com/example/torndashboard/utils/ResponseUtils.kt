package com.example.torndashboard.utils

import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response


data class MoneyResponse(
    val money_onhand: Int
)

data class StatsResponse(
    val energy: StatsInfo,
    val nerve: StatsInfo,
    val happy: StatsInfo,
    val life: StatsInfo,
)

data class StatsInfo(
    val current: Int,
    val maximum: Int,
    val fulltime: Int
)
data class CooldownsResponse(
    val cooldowns: CooldownsInfo
)
data class CooldownsInfo(
    val drug: Int,
    val booster: Int,
    val medical: Int
)

data class travelResponse(
    val travel: travalInfo
)
data class travalInfo(
    val time_left: Int
)

class ApiResponseCallback<T>(
    private val onSuccess: (T?) -> Unit,
    private val onError: (Throwable) -> Unit
) : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            onSuccess(response.body())
        } else {
            onError(HttpException(response))
        }
    }
    override fun onFailure(call: Call<T>, t: Throwable) {
        onError(t)
    }
}
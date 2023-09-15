package com.example.torndashboard.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.example.torndashboard.R
import com.example.torndashboard.config.AppConfig.maxTime
import com.example.torndashboard.config.AppConfig.timeFilter
import com.example.torndashboard.utils.ApiResponseCallback
import com.example.torndashboard.utils.CooldownsResponse
import com.example.torndashboard.utils.MoneyResponse
import com.example.torndashboard.web.RetrofitClient
import com.example.torndashboard.utils.StatsResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WidgetProvider : AppWidgetProvider() {
    private lateinit var views: RemoteViews
    private var minTime: Int = maxTime

    private fun getCurrentTimeFormatted(): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val currentTime = Date()
        return dateFormat.format(currentTime)
    }

    private fun setRemoteViewsText(id: Int, string: String) {
        views.setTextViewText(id, string)
    }

    private fun update(views: RemoteViews, context: Context?, appWidgetId: Int) {
        val appWidgetManager = AppWidgetManager.getInstance(context)

        updateStats(views, appWidgetManager, appWidgetId)
        updateMoney(views, appWidgetManager, appWidgetId)
        updateCooldowns(views, appWidgetManager, appWidgetId)

        val formattedTime = getCurrentTimeFormatted()
        views.setTextViewText(R.id.currentUpdateTextView, formattedTime)

        appWidgetManager.updateAppWidget(appWidgetId, views)

        minTime = maxTime
    }
    private fun updateCooldowns(
        views: RemoteViews,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int) {
        val cooldownsCall = RetrofitClient.apiService.getCooldownsInfo()

        cooldownsCall.enqueue(ApiResponseCallback<CooldownsResponse>(
            onSuccess = { cooldownsResponse ->
                val cooldowns = cooldownsResponse?.cooldowns

                val drug = cooldowns?.drug ?: maxTime
                val booster = cooldowns?.booster?: maxTime
                val medical = cooldowns?.medical?: maxTime

                updateTimeTextView(R.id.currentDrugTextView,drug)
                updateTimeTextView(R.id.currentBoosterTextView,booster)
                updateTimeTextView(R.id.currentMedicalTextView,medical)

                val timeArray = intArrayOf(drug, booster, medical)

                var i = 4
                while (i < 7){
                    if (timeFilter[i]) {
                        minTime = if (timeArray[i-4] < minTime) timeArray[i-4] else minTime
                    }
                    i++
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
            },
            onError = { error ->
                Log.d("MyApp", error.toString())
                error.printStackTrace()
            }
        ))
    }

    private fun updateTimeTextView(id: Int, time:Int) {
        if (time == 0){
            setRemoteViewsText(id, "可")
        }else{
            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val currentTime = Date()
            val newTime = Date(currentTime.time + (time * 1000L))

            setRemoteViewsText(id, dateFormat.format(newTime))
        }
    }

    private fun updateMoney(
        views: RemoteViews,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val moneyCall = RetrofitClient.apiService.getMoneyInfo()

        moneyCall.enqueue(ApiResponseCallback<MoneyResponse>(
            onSuccess = { moneyResponse ->
                val moneyOnHand = "$" + (moneyResponse?.money_onhand ?: -1).toString()

                setRemoteViewsText(R.id.currentWalletTextView, moneyOnHand)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            },
            onError = { error ->
                error.printStackTrace()
            }
        ))
    }

    private fun updateStats(
        views: RemoteViews,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val statsCall = RetrofitClient.apiService.getStatsInfo()

        statsCall.enqueue(ApiResponseCallback<StatsResponse>(
            onSuccess = { statsResponse ->
                val energyStats = statsResponse?.energy
                val nerveStats = statsResponse?.nerve
                val happyStats = statsResponse?.happy
                val lifeStats = statsResponse?.life

                val timeArray = intArrayOf(
                    energyStats?.fulltime ?: maxTime,
                    nerveStats?.fulltime ?: maxTime,
                    happyStats?.fulltime ?: maxTime,
                    lifeStats?.fulltime ?: maxTime,
                )

                updateStatsView(
                    R.id.currentEnergyTextView,
                    R.id.energyProgressBar,
                    energyStats?.current ?: -1,
                    energyStats?.maximum ?: -1
                )
                updateStatsView(
                    R.id.currentNerveTextView,
                    R.id.nerveProgressBar,
                    nerveStats?.current ?: -1,
                    nerveStats?.maximum ?: -1
                )
                updateStatsView(
                    R.id.currentHappyTextView,
                    R.id.happyProgressBar,
                    happyStats?.current?: -1,
                    happyStats?.maximum?: -1
                )
                updateStatsView(
                    R.id.currentLifeTextView,
                    R.id.lifeProgressBar,
                    lifeStats?.current?: -1,
                    lifeStats?.maximum?: -1
                )

                var i = 0
                while (i < 4) {
                    if (timeFilter[i]) {
                        minTime = if (timeArray[i] < minTime) timeArray[i] else minTime
                    }
                    i++
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
            },
            onError = { error ->
                error.printStackTrace()
            }
        ))
    }

    private fun updateStatsView(textId: Int, barId: Int, current: Int, maximum: Int) {
        views.setTextViewText(textId, "$current/$maximum")
        views.setProgressBar(barId, 100, 100 * current / maximum, false)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            views = RemoteViews(context.packageName, R.layout.widget_layout)

            val intent = Intent(context, WidgetProvider::class.java)
            intent.action = "BUTTON_CLICK"
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val pendingIntent = PendingIntent.getBroadcast(
                context, appWidgetId, intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.currentUpdateTextView, pendingIntent)
            views.setOnClickPendingIntent(R.id.updateTextView, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent?.action == "BUTTON_CLICK") {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )

            views = RemoteViews(context?.packageName, R.layout.widget_layout)

            update(views, context, appWidgetId)
        }
    }
}

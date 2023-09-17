package com.example.torndashboard.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.text.Html
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.example.torndashboard.MainActivity
import com.example.torndashboard.R
import com.example.torndashboard.config.AppConfig
import com.example.torndashboard.config.AppConfig.maxTime
import com.example.torndashboard.config.AppConfig.timeFilter
import com.example.torndashboard.config.AppConfig.timeIsZeroTextVisibility
import com.example.torndashboard.config.AppConfig.timeMinText
import com.example.torndashboard.preferences.WidgetProviderSharedPreferences
import com.example.torndashboard.utils.ApiResponseCallback
import com.example.torndashboard.utils.CooldownsResponse
import com.example.torndashboard.utils.ErrorResponse
import com.example.torndashboard.utils.EventsResponse
import com.example.torndashboard.utils.Item
import com.example.torndashboard.utils.MoneyResponse
import com.example.torndashboard.utils.NotificationReceiver
import com.example.torndashboard.utils.StatsResponse
import com.example.torndashboard.utils.TravelResponse
import com.example.torndashboard.utils.getCurrentTimeFormatted
import com.example.torndashboard.utils.getMinTimeHHMMFormatted
import com.example.torndashboard.utils.itemsList
import com.example.torndashboard.utils.showNotification
import com.example.torndashboard.web.RetrofitClient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class WidgetProvider : AppWidgetProvider() {
    private lateinit var views: RemoteViews
    private var minTime: Int = maxTime
    private var minText: String = ""


    private fun setRemoteViewsText(id: Int, string: String) {
        views.setTextViewText(id, string)
    }

    private fun update(views: RemoteViews, context: Context?, appWidgetId: Int) {
        val appWidgetManager = AppWidgetManager.getInstance(context)

        val widgetProviderSharedPreferences = context?.let { WidgetProviderSharedPreferences(it) }
        var lastUpdateTime = widgetProviderSharedPreferences?.getLastUpdateTime() ?: -1

        val t = System.currentTimeMillis() - lastUpdateTime

        updateMoney(views, appWidgetManager, appWidgetId)
        updateEvents(views, appWidgetManager, appWidgetId)

        if (lastUpdateTime == 0.toLong() || t > 30000) {
            minText = ""
            minTime = maxTime


            widgetProviderSharedPreferences?.let {
                updateStats(views, appWidgetManager, appWidgetId,it)
                updateCooldowns(views, appWidgetManager, appWidgetId, it)
                updateTravel(views, appWidgetManager, appWidgetId, it)
            }

            setRemoteViewsText(R.id.currentUpdateTextView, getCurrentTimeFormatted())

            lastUpdateTime = System.currentTimeMillis()
            widgetProviderSharedPreferences?.saveLastUpdateTime(lastUpdateTime)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        } else {
            updateError("距离上次更新间隔为${t / 1000}s，不足30s，返回上次结果！")
        }
    }

    private fun updateEvents(
        views: RemoteViews,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val eventsCall = RetrofitClient.apiService.getEventsInfo()

        eventsCall.enqueue(ApiResponseCallback<EventsResponse>(
            onSuccess = { eventsResponse ->
                val eventsCount = eventsResponse?.events?.size ?: -1

                setRemoteViewsText(R.id.currentEventTextView, eventsCount.toString())

                eventsResponse?.let { checkResponseError(it) }

                if (eventsCount != -1 && eventsResponse?.events != null) {
                    for ((_, eventData) in eventsResponse.events) {
                        val timestamp = eventData.timestamp
                        val eventText = eventData.event

                        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

                        val item = Item(
                            "Event",
                            "\t\t${Html.fromHtml(eventText, Html.FROM_HTML_MODE_LEGACY)}",
                            dateFormat.format(timestamp * 1000)
                        )

                        itemsList.add(item)
                    }
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
            },
            onError = { error ->
                updateError(error.toString())
                error.printStackTrace()
            }
        ))
    }

    private fun checkResponseError(eventsResponse: ErrorResponse) {
        val errorResponse = eventsResponse.error
        if (errorResponse?.error != null) {
            updateError("code: ${errorResponse.code}\nerror: ${errorResponse.error}")
        }
    }

    private fun updateTravel(
        views: RemoteViews,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        widgetProviderSharedPreferences: WidgetProviderSharedPreferences
    ) {
        val travelCall = RetrofitClient.apiService.getTravelInfo()


        travelCall.enqueue(ApiResponseCallback<TravelResponse>(
            onSuccess = { travelResponse ->
                val travel = travelResponse?.travel

                val timeLeft = travel?.time_left ?: maxTime

                travelResponse?.let { checkResponseError(it) }

                updateMinTime(intArrayOf(timeLeft), 7, widgetProviderSharedPreferences)

                updateTimeTextView(R.id.currentTravelTextView, timeLeft)

                appWidgetManager.updateAppWidget(appWidgetId, views)


            },
            onError = { error ->
                updateError(error.toString())
                error.printStackTrace()
            }
        ))
    }

    private fun updateCooldowns(
        views: RemoteViews,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        widgetProviderSharedPreferences: WidgetProviderSharedPreferences
    ) {
        val cooldownsCall = RetrofitClient.apiService.getCooldownsInfo()

        cooldownsCall.enqueue(ApiResponseCallback<CooldownsResponse>(
            onSuccess = { cooldownsResponse ->
                val cooldowns = cooldownsResponse?.cooldowns

                val drug = cooldowns?.drug ?: maxTime
                val booster = cooldowns?.booster ?: maxTime
                val medical = cooldowns?.medical ?: maxTime

                cooldownsResponse?.let { checkResponseError(it) }

                updateTimeTextView(R.id.currentDrugTextView, drug)
                updateTimeTextView(R.id.currentBoosterTextView, booster)
                updateTimeTextView(R.id.currentMedicalTextView, medical)

                val timeArray = intArrayOf(drug, booster, medical)

                updateMinTime(timeArray, 4, widgetProviderSharedPreferences)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            },
            onError = { error ->
                updateError(error.toString())
                error.printStackTrace()
            }
        ))
    }

    private fun updateError(error: String) {
        val item = Item("Error", error, getCurrentTimeFormatted())

        itemsList.add(0, item)
    }

    private fun updateMinTime(
        timeArray: IntArray, start: Int = 0,
        widgetProviderSharedPreferences: WidgetProviderSharedPreferences
    ) {
        var i = start
        val end = start + timeArray.size
        while (i < end) {
            val current = i - start

            if (timeFilter[i] && timeArray[current] <= minTime && (timeArray[current] != 0 || timeIsZeroTextVisibility[i])) {
                if (timeArray[i - start] <= minTime) {
                    minTime = timeArray[current]
                    if (timeArray[current] == 0) {
                        minText += timeMinText[i]
                    }

                    widgetProviderSharedPreferences.saveMinTime(minTime)

                    updateTimeTextView(R.id.currentMinTextView, minTime, minText)
                }
            }
            i++
        }
    }

    private fun updateTimeTextView(id: Int, time: Int, text: String = "可") {
        if (time == 0) {
            setRemoteViewsText(id, text)
        } else {
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

                moneyResponse?.let { checkResponseError(it) }

                setRemoteViewsText(R.id.currentWalletTextView, moneyOnHand)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            },
            onError = { error ->
                updateError(error.toString())
                error.printStackTrace()
            }
        ))
    }

    private fun updateStats(
        views: RemoteViews,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        widgetProviderSharedPreferences: WidgetProviderSharedPreferences
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

                statsResponse?.let { checkResponseError(it) }

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
                    happyStats?.current ?: -1,
                    happyStats?.maximum ?: -1
                )
                updateStatsView(
                    R.id.currentLifeTextView,
                    R.id.lifeProgressBar,
                    lifeStats?.current ?: -1,
                    lifeStats?.maximum ?: -1
                )

                updateMinTime(timeArray, widgetProviderSharedPreferences = widgetProviderSharedPreferences)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            },
            onError = { error ->
                updateError(error.toString())
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

            setupUpdateClickEvent(context, appWidgetId)
            setupEventClickEvent(context, appWidgetId)
            setupMinClickEvent(context, appWidgetId)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun setupEventClickEvent(context: Context, appWidgetId: Int) {
        val intent = Intent(context, WidgetProvider::class.java)
        intent.action = "EVENT_CLICK"
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        val pendingIntent = PendingIntent.getBroadcast(
            context, appWidgetId, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.eventTextView, pendingIntent)
        views.setOnClickPendingIntent(R.id.currentEventTextView, pendingIntent)
        views.setOnClickPendingIntent(R.id.eventLinearLayout, pendingIntent)
    }

    private fun setupUpdateClickEvent(context: Context, appWidgetId: Int) {
        val intent = Intent(context, WidgetProvider::class.java)
        intent.action = "UPDATE_CLICK"
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        val pendingIntent = PendingIntent.getBroadcast(
            context, appWidgetId, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.currentUpdateTextView, pendingIntent)
        views.setOnClickPendingIntent(R.id.updateTextView, pendingIntent)
    }

    private fun setupMinClickEvent(context: Context, appWidgetId: Int) {
        val intent = Intent(context, WidgetProvider::class.java)
        intent.action = "MIN_CLICK"
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

        val pendingIntent = PendingIntent.getBroadcast(
            context, appWidgetId, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        views.setOnClickPendingIntent(R.id.minTextView, pendingIntent)
        views.setOnClickPendingIntent(R.id.currentMinTextView, pendingIntent)
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        context?.let { RetrofitClient.checkApiKey(it) }

        when (intent?.action) {
            "UPDATE_CLICK" -> {
                val appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )

                context?.let { AppConfig.initialize(context) }

                views = RemoteViews(context?.packageName, R.layout.widget_layout)

                update(views, context, appWidgetId)
            }

            "EVENT_CLICK" -> {
                val mainActivityIntent = Intent(context, MainActivity::class.java)
                mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                context?.startActivity(mainActivityIntent)
            }

            "MIN_CLICK" -> {
                try {
                    val widgetProviderSharedPreferences =
                        context?.let { WidgetProviderSharedPreferences(it) }

                    when (val lastMinTime = widgetProviderSharedPreferences?.getMinTime() ?: -1) {
                        -1 -> {
                            updateError("奇怪错误！我修不了这个")
                        }

                        0 -> {
                            updateError("摸鱼时间为0，无法设置闹钟！")
                        }

                        else -> {
                            val alarmIntent = Intent(AlarmClock.ACTION_SET_ALARM)
                            alarmIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                            val timeString = getMinTimeHHMMFormatted(lastMinTime)

                            val parts = timeString.split(":")

                            if (parts.size == 2) {
                                alarmIntent.putExtra(AlarmClock.EXTRA_HOUR, 1)
                                alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, 1)

                                showNotification(context)

                                context?.startActivity(alarmIntent)
                            } else {
                                updateError("摸鱼时间为$timeString，格式错误！")
                            }
                        }
                    }
                } catch (e: Exception) {
                    updateError(e.toString())
                    e.printStackTrace()
                }
            }
        }
    }
}

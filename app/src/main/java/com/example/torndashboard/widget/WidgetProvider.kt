package com.example.torndashboard.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.text.Html
import android.widget.RemoteViews
import com.example.torndashboard.MainActivity
import com.example.torndashboard.R
import com.example.torndashboard.utils.itemsList
import com.example.torndashboard.config.AppConfig.maxTime
import com.example.torndashboard.config.AppConfig.timeFilter
import com.example.torndashboard.config.AppConfig.timeIsZeroTextVisibility
import com.example.torndashboard.config.AppConfig.timeMinText
import com.example.torndashboard.utils.ApiResponseCallback
import com.example.torndashboard.utils.CooldownsResponse
import com.example.torndashboard.utils.EventsResponse
import com.example.torndashboard.utils.Item
import com.example.torndashboard.utils.MoneyResponse
import com.example.torndashboard.web.RetrofitClient
import com.example.torndashboard.utils.StatsResponse
import com.example.torndashboard.utils.TravelResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WidgetProvider : AppWidgetProvider() {
    private lateinit var views: RemoteViews
    private var minTime: Int = maxTime
    private var minText: String = ""

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

        minText = ""

        updateStats(views, appWidgetManager, appWidgetId)
        updateMoney(views, appWidgetManager, appWidgetId)
        updateCooldowns(views, appWidgetManager, appWidgetId)
        updateTravel(views, appWidgetManager, appWidgetId)
        updateEvents(views, appWidgetManager, appWidgetId)

        setRemoteViewsText(R.id.currentUpdateTextView, getCurrentTimeFormatted())

        appWidgetManager.updateAppWidget(appWidgetId, views)

        minTime = maxTime
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

                if (eventsCount != -1 && eventsResponse?.events != null) {
                    for ((_, eventData) in eventsResponse.events) {
                        val timestamp = eventData.timestamp
                        val eventText = eventData.event

                        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

                        val item = Item(
                            "Event",
                            "\t\t${Html.fromHtml(eventText, Html.FROM_HTML_MODE_LEGACY)}",
                            dateFormat.format(timestamp*1000)
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

    private fun updateTravel(
        views: RemoteViews,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val travelCall = RetrofitClient.apiService.getTravelInfo()

        travelCall.enqueue(ApiResponseCallback<TravelResponse>(
            onSuccess = { travelResponse ->
                val travel = travelResponse?.travel

                val timeLeft = travel?.time_left ?: maxTime

                updateMinTime(intArrayOf(timeLeft), 7)

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
        appWidgetId: Int
    ) {
        val cooldownsCall = RetrofitClient.apiService.getCooldownsInfo()

        cooldownsCall.enqueue(ApiResponseCallback<CooldownsResponse>(
            onSuccess = { cooldownsResponse ->
                val cooldowns = cooldownsResponse?.cooldowns

                val drug = cooldowns?.drug ?: maxTime
                val booster = cooldowns?.booster ?: maxTime
                val medical = cooldowns?.medical ?: maxTime

                updateTimeTextView(R.id.currentDrugTextView, drug)
                updateTimeTextView(R.id.currentBoosterTextView, booster)
                updateTimeTextView(R.id.currentMedicalTextView, medical)

                val timeArray = intArrayOf(drug, booster, medical)

                updateMinTime(timeArray, 4)

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

        itemsList.add(item)
    }

    private fun updateMinTime(timeArray: IntArray, start: Int = 0) {
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
                    happyStats?.current ?: -1,
                    happyStats?.maximum ?: -1
                )
                updateStatsView(
                    R.id.currentLifeTextView,
                    R.id.lifeProgressBar,
                    lifeStats?.current ?: -1,
                    lifeStats?.maximum ?: -1
                )

                updateMinTime(timeArray)

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

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun setupEventClickEvent(context: Context, appWidgetId: Int) {
        val intent = Intent(context, WidgetProvider::class.java)
        intent.action = "Event_CLICK"
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

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        context?.let { RetrofitClient.checkApiKey(it) }

        if (intent?.action == "UPDATE_CLICK") {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )

            views = RemoteViews(context?.packageName, R.layout.widget_layout)

            update(views, context, appWidgetId)
        } else if (intent?.action == "Event_CLICK") {
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            context?.startActivity(mainActivityIntent)
        }
    }
}

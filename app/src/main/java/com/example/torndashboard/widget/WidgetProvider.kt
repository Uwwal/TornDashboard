package com.example.torndashboard.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import com.example.torndashboard.R
var t =0
class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            val intent = Intent(context, WidgetProvider::class.java)
            intent.action = "UPDATE_CLICK"
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent,
                PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.currentUpdateTextView, pendingIntent)
            views.setOnClickPendingIntent(R.id.updateTextView, pendingIntent)

            views.setTextViewText(R.id.currentUpdateTextView, t.toString())

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent?.action == "UPDATE_CLICK") {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

            t += 1

            val views = RemoteViews(context?.packageName, R.layout.widget_layout)
            views.setTextViewText(R.id.currentUpdateTextView, t.toString())

            val appWidgetManager = AppWidgetManager.getInstance(context)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

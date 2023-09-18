package com.example.torndashboard.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.torndashboard.MainActivity
import com.example.torndashboard.R

class ClockNotification {
    private val channelId = "TornDashBoard"
    private val textTitle = "TornDashBoard"

    private fun createNotification(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, textTitle, NotificationManager.IMPORTANCE_HIGH)
            channel.description = "闹钟时间"
            val notificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    fun showNotification(context: Context, text: String) {
        val notification = ClockNotification()
        notification.createNotification(context)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(textTitle)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    context.startActivity(intent)
                    return
                }
            }

            val fullScreenIntent = Intent(context, MainActivity::class.java)
            val fullScreenPendingIntent = PendingIntent.getActivity(
                context, 0, fullScreenIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
            builder.setFullScreenIntent(fullScreenPendingIntent, true)

            notify(1, builder.build())
        }
    }
}

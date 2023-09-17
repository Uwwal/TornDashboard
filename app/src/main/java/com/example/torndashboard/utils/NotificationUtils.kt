package com.example.torndashboard.utils

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.SystemClock
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        createNotificationChannel(context)

        val channelId = "your_channel_id"
        val notificationBuilder = NotificationCompat.Builder(context!!, channelId)
            .setContentTitle("Your Channel Name")
            .setContentText("Your channel description")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        val notificationId = 1
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}

fun createNotificationChannel(context: Context?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "your_channel_id"
        val channelName = "Your Channel Name"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channelDescription = "Your channel description"

        val notificationChannel = NotificationChannel(channelId, channelName, importance)
        notificationChannel.description = channelDescription

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }
}

fun showNotification(context: Context?) {
    val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val notificationIntent = Intent(context, NotificationReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent,
        PendingIntent.FLAG_IMMUTABLE)

    val triggerTimeMillis = SystemClock.elapsedRealtime()

    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTimeMillis, pendingIntent)
}
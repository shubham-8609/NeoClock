package com.codeleg.neoclock.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.codeleg.neoclock.R

object NotificationHelper {
    const val CHANNEL_ID = "alarm_channel"
    const val NOTIF_ID = 1001

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(NotificationManager::class.java)
            val ch = NotificationChannel(
                CHANNEL_ID,
                "Alarms",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alarm notifications"
                // We use MediaPlayer for playback, so keep channel silent
                setSound(null, null)
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            nm.createNotificationChannel(ch)
        }
    }

    fun buildAlarmNotification(
        context: Context,
        title: String,
        content: String,
        fullScreenPendingIntent: android.app.PendingIntent?
    ): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            // Max priority & alarm category so the system treats this as an alarm
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .setOngoing(true)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
            .apply {
                if (fullScreenPendingIntent != null) {
                    // This is what allows AlarmActivity to pop over the lock screen
                    setFullScreenIntent(fullScreenPendingIntent, true)
                    // Also set as regular content intent for when user taps the notification
                    setContentIntent(fullScreenPendingIntent)
                }
            }
            .build()
    }
}

package com.codeleg.neoclock.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.codeleg.neoclock.NeoClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (
            action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
            action == Intent.ACTION_TIMEZONE_CHANGED ||
            action == Intent.ACTION_TIME_CHANGED // use available constant
        ) {
            // reschedule alarms on background coroutine
            CoroutineScope(Dispatchers.IO).launch {
                val app = context.applicationContext as NeoClock
                val repo = app.alarmRepo
                AlarmScheduler.rescheduleAll(context, repo)
            }
        }
    }
}

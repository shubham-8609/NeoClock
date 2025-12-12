package com.codeleg.neoclock.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.codeleg.neoclock.alarm.AlarmRingingService

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra("alarm_id", -1)
        if (id != -1) {
            // Start the ringing service which will show notification and play sound
            AlarmRingingService.startRinging(context, id)
        }
    }
}

package com.codeleg.neoclock.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.codeleg.neoclock.database.model.Alarm
import com.codeleg.neoclock.repository.AlarmRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime

/**
 * Centralized alarm scheduling helper.
 * - computes next trigger
 * - schedules/cancels alarms via AlarmManager
 * - small, minimal implementation suitable for extension
 */
object AlarmScheduler {
    private const val EXTRA_ALARM_ID = "alarm_id"

    suspend fun scheduleAlarm(context: Context, repository: AlarmRepository, alarm: Alarm) {
        val next = computeNextTriggerTime(alarm.hour, alarm.minute, alarm.repeatDays)
        // update alarm nextTriggerTime in DB
        val updated = alarm.copy(nextTriggerTime = next)
        withContext(Dispatchers.IO) {
            repository.updateAlarm(updated)
        }

        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent(context, AlarmReceiver::class.java).apply { putExtra(EXTRA_ALARM_ID, alarm.id) }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pi = PendingIntent.getBroadcast(context, alarm.id, intent, flags)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next, pi)
    }

    fun cancelAlarm(context: Context, alarmId: Int) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent(context, AlarmReceiver::class.java).apply { putExtra(EXTRA_ALARM_ID, alarmId) }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pi = PendingIntent.getBroadcast(context, alarmId, intent, flags)
        alarmManager.cancel(pi)
        pi.cancel()
    }

    suspend fun rescheduleAll(context: Context, repository: AlarmRepository) {
        // load all enabled alarms and schedule each
        withContext(Dispatchers.IO) {
            val alarms = repository.getEnabledAlarms()
            alarms.filter { it.isEnabled }.forEach { scheduleAlarm(context, repository, it) }
        }
    }

    fun computeNextTriggerTime(hour: Int, minute: Int, repeatDays: List<Int>, from: ZonedDateTime = ZonedDateTime.now()): Long {
        var candidate = from.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
        if (repeatDays.isEmpty()) {
            if (candidate.isBefore(from) || candidate.isEqual(from)) candidate = candidate.plusDays(1)
            return candidate.toInstant().toEpochMilli()
        }
        for (offset in 0..6) {
            val c = candidate.plusDays(offset.toLong())
            val weekday = (c.dayOfWeek.value % 7) // MON=1..SUN=7 => mapping to 1..7 then %7 gives 0..6 where Sunday=0
            if (repeatDays.contains(weekday) && (c.isAfter(from) || c.isEqual(from))) {
                return c.toInstant().toEpochMilli()
            }
        }
        return candidate.plusDays(1).toInstant().toEpochMilli()
    }
}

package com.codeleg.neoclock.repository

import androidx.lifecycle.LiveData
import com.codeleg.neoclock.database.local.AlarmDao
import com.codeleg.neoclock.database.model.Alarm

class AlarmRepository(private val alarmDao: AlarmDao) {

    // Get all alarms (sorted however you like)
    val allAlarms = alarmDao.getAllAlarms()

    // Insert a new alarm — return row id so callers can schedule immediately
    suspend fun insertAlarm(alarm: Alarm): Long {
        return alarmDao.insertAlarm(alarm)
    }

    // Delete alarm
    suspend fun deleteAlarm(alarm: Alarm) {
        alarmDao.deleteAlarm(alarm)
    }

    // Update alarm (e.g., label, time, repeatDays, isEnabled)
    suspend fun updateAlarm(alarm: Alarm) {
        alarmDao.updateAlarm(alarm)
    }

    // Update only specific properties (optional)
    suspend fun updateAlarmEnabled(id: Int, enabled: Boolean) {
        alarmDao.updateAlarmEnabled(id, enabled)
    }

    // Get a specific alarm by ID (suspend)
    suspend fun getAlarmById(id: Int): Alarm? {
        return alarmDao.getAlarmById(id)
    }

    // Expose LiveData for single alarm — useful for UI observers
    fun getAlarmByIdLive(id: Int): LiveData<Alarm?> = alarmDao.getAlarmByIdLive(id)

    // Expose enabled alarms (suspend) for rescheduling
    suspend fun getEnabledAlarms(): List<Alarm> = alarmDao.getEnabledAlarms()
}

package com.codeleg.neoclock.repository

import com.codeleg.neoclock.database.local.AlarmDao
import com.codeleg.neoclock.database.model.Alarm

class AlarmRepository(private val alarmDao: AlarmDao) {

    // Get all alarms (sorted however you like)
    val allAlarms = alarmDao.getAllAlarms()

    // Insert a new alarm
    suspend fun insertAlarm(alarm: Alarm) {
        alarmDao.insertAlarm(alarm)
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

    // Get a specific alarm by ID
    suspend fun getAlarmById(id: Int): Alarm? {
        return alarmDao.getAlarmById(id)
    }
}

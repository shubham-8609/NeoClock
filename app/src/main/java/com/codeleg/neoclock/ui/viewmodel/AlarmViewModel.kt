package com.codeleg.neoclock.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.codeleg.neoclock.alarm.AlarmScheduler
import com.codeleg.neoclock.database.model.Alarm
import com.codeleg.neoclock.repository.AlarmRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmViewModel(private val repository: AlarmRepository, application: Application) : AndroidViewModel(application) {

    // Expose all alarms to the UI
    val allAlarms = repository.allAlarms

    // Insert new alarm and return inserted id via callback (keeps compatibility)
    fun insertAlarm(alarm: Alarm, onInserted: ((Long) -> Unit)? = null) = viewModelScope.launch {
        val id = repository.insertAlarm(alarm)
        onInserted?.invoke(id)

        // Schedule the alarm after insert if enabled
        val saved = alarm.copy(id = id.toInt())
        if (saved.isEnabled) {
            // schedule on IO
            launch(Dispatchers.IO) {
                AlarmScheduler.scheduleAlarm(getApplication(), repository, saved)
            }
        }
    }

    // Update the entire alarm and reschedule/cancel accordingly
    fun updateAlarm(alarm: Alarm) = viewModelScope.launch {
        repository.updateAlarm(alarm)
        if (alarm.isEnabled) {
            launch(Dispatchers.IO) { AlarmScheduler.scheduleAlarm(getApplication(), repository, alarm) }
        } else {
            AlarmScheduler.cancelAlarm(getApplication(), alarm.id)
        }
    }

    // Delete alarm
    fun deleteAlarm(alarm: Alarm) = viewModelScope.launch {
        repository.deleteAlarm(alarm)
        // cancel scheduled alarm if present
        AlarmScheduler.cancelAlarm(getApplication(), alarm.id)
    }

    // Enable/disable alarm (only updates isEnabled)
    fun updateEnabled(id: Int, enabled: Boolean) = viewModelScope.launch {
        repository.updateAlarmEnabled(id, enabled)
        if (enabled) {
            // fetch alarm and schedule
            val alarm = withContext(Dispatchers.IO) { repository.getAlarmById(id) }
            if (alarm != null) launch(Dispatchers.IO) { AlarmScheduler.scheduleAlarm(getApplication(), repository, alarm) }
        } else {
            AlarmScheduler.cancelAlarm(getApplication(), id)
        }
    }

    // Get alarm by id (suspend helper)
    suspend fun getAlarmByIdSuspend(id: Int): Alarm? = withContext(Dispatchers.IO) {
        repository.getAlarmById(id)
    }

    // Provide LiveData for observing single alarm in UI
    fun getAlarmLive(id: Int): LiveData<Alarm?> = repository.getAlarmByIdLive(id)
}

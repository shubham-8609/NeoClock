package com.codeleg.neoclock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeleg.neoclock.database.model.Alarm
import com.codeleg.neoclock.repository.AlarmRepository
import kotlinx.coroutines.launch

class AlarmViewModel(private val repository: AlarmRepository) : ViewModel() {

    // Expose all alarms to the UI
    val allAlarms = repository.allAlarms

    // Insert new alarm
    fun insertAlarm(alarm: Alarm) = viewModelScope.launch {
        repository.insertAlarm(alarm)
    }

    // Update the entire alarm
    fun updateAlarm(alarm: Alarm) = viewModelScope.launch {
        repository.updateAlarm(alarm)
    }

    // Delete alarm
    fun deleteAlarm(alarm: Alarm) = viewModelScope.launch {
        repository.deleteAlarm(alarm)
    }

    // Enable/disable alarm (only updates isEnabled)
    fun updateEnabled(id: Int, enabled: Boolean) = viewModelScope.launch {
        repository.updateAlarmEnabled(id, enabled)
    }

    // Get alarm by id
    fun getAlarmById(id: Int, callback: (Alarm?) -> Unit) {
        viewModelScope.launch {
            val alarm = repository.getAlarmById(id)
            callback(alarm)
        }
    }
}

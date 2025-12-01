package com.codeleg.neoclock.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class Alarm(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Alarm time
    val hour: Int,
    val minute: Int,

    // Whether alarm is enabled
    val isEnabled: Boolean = true,

    // Days when alarm repeats (Sun = 0 … Sat = 6)
    val repeatDays: List<Int> = emptyList(),

    // Optional label shown under the time
    val label: String = "",

    // Whether vibration should be used
    val vibrate: Boolean = true,

    // Alarm tone URI or default
    val ringtoneUri: String = "",

    // Computed next trigger timestamp (System.currentTimeMillis())
    val nextTriggerTime: Long = 0L,

    // Snooze duration in minutes
    val snoozeMinutes: Int = 10
)
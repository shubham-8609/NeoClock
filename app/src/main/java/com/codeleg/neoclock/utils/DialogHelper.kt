package com.codeleg.neoclock.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

object DialogHelper {

    fun showTimePicker(
        context: Context,
        initialHour: Int = 8,
        initialMinute: Int = 0,
        onTimeSelected: (hour: Int, minute: Int) -> Unit
    ) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(initialHour)
            .setMinute(initialMinute)
            .setTitleText("Select Alarm Time")
            .build()

        picker.addOnPositiveButtonClickListener {
            onTimeSelected(picker.hour, picker.minute)
        }

        picker.show((context as AppCompatActivity).supportFragmentManager, "TIME_PICKER")
    }
}
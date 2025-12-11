package com.codeleg.neoclock

import android.app.Application
import com.codeleg.neoclock.database.local.DBHelper
import com.codeleg.neoclock.repository.AlarmRepository
import com.google.android.material.color.DynamicColors

class NeoClock: Application() {
    val database by lazy { DBHelper.getDatabase(this) }
    val alarmRepo by lazy { AlarmRepository(database.AlarmDao()) }


    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
package com.codeleg.neoclock

import android.app.Application
import com.codeleg.neoclock.database.local.DBHelper

class NeoClock: Application() {
    val database by lazy { DBHelper.getDatabase(this) }
}
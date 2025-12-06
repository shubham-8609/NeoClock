package com.codeleg.neoclock.database.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.codeleg.neoclock.database.model.Alarm

@Database(entities = [Alarm::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class DBHelper : RoomDatabase() {

    abstract fun AlarmDao(): AlarmDao

    companion object {
        @Volatile
        private var INSTANCE: DBHelper? = null

        fun getDatabase(context: Context): DBHelper {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DBHelper::class.java,
                    "alarm_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }

    }

}
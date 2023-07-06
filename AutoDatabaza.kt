package com.example.spravcavozidiel.Databaza

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Abstraktná trieda `CarDatabase` je RoomDatabáza, ktorá zahŕňa `Auto` a `UdrzbaZaznam` ako entity.
 */

@Database(entities = [Auto::class, UdrzbaZaznam::class], version = 2, exportSchema = false)
abstract class AutoDatabaza : RoomDatabase() {

    abstract fun autoDao(): AutoDao
    abstract fun udrzbaZaznamDao(): UdrzbaZaznamDao

    companion object {
        @Volatile
        private var INSTANCE: AutoDatabaza? = null

        fun getDatabase(context: Context): AutoDatabaza {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AutoDatabaza::class.java,
                    "car_maintenance.db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

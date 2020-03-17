package com.ey.weatherforecastapp.ui.main.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ey.weatherforecastapp.ui.main.model.data.WeatherData
import com.ey.weatherforecastapp.ui.main.model.database.dao.WeatherDao

@Database(entities = [WeatherData::class], version = 1, exportSchema = false)
abstract class WeatherDatabase:RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    companion object {

        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabase(context: Context): WeatherDatabase? {
            if (INSTANCE == null) {
                synchronized(WeatherDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            WeatherDatabase::class.java, "weather_database"
                        )
                            .build()
                    }
                }
            }
            return INSTANCE
        }
    }

}
package com.ey.weatherforecastapp.ui.main.model.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ey.weatherforecastapp.ui.main.model.data.WeatherData

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeatherData(weatherData: WeatherData)

    @Query("SELECT * from current_weather")
    fun getWeather(): LiveData<WeatherData>

    @Update
    fun updateWeatherData(weatherData: WeatherData)
}
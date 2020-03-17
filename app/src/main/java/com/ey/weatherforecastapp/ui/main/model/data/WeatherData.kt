package com.ey.weatherforecastapp.ui.main.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey

const val CURRENT_WEATHER_ID = 0

@Entity(tableName = "current_weather")
data class WeatherData(
        var dateTime: String = "",
        var temperature: String = "0",
        var cityAndCountry: String = "",
        var weatherConditionIconUrl: String = "",
        var weatherConditionIconDescription: String = "",
        var humidity: String = "",
        var pressure: String = "",
        var visibility: String = "",
        var sunrise: String = "",
        var sunset: String = "",
        var description: String = "",
        var windSpeed: String = "",
        var windDegree: String = ""
){
        @PrimaryKey(autoGenerate = false)
        var id: Int = com.resocoder.forecastmvvm.data.db.entity.CURRENT_WEATHER_ID
}
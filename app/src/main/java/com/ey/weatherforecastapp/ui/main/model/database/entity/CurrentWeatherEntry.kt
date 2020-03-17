package com.resocoder.forecastmvvm.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

const val CURRENT_WEATHER_ID = 0

@Entity(tableName = "current_weather")
data class CurrentWeatherEntry(
    @SerializedName("dateTime")
    var dateTime: String,
    @SerializedName("temperature")
    var temperature: String,
    @SerializedName("cityAndCountry")
    var cityAndCountry: String,
    @SerializedName("weatherConditionIconUrl")
    var weatherConditionIconUrl: String,
    @SerializedName("weatherConditionIconDescription")
    var weatherConditionIconDescription: String,
    @SerializedName("humidity")
    var humidity: String,
    @SerializedName("pressure")
    var pressure: String,
    @SerializedName("visibility")
    var visibility: String,
    @SerializedName("sunrise")
    var sunrise: String,
    @SerializedName("sunset")
    var sunset: String,
    @SerializedName("description")
    var description: String,
    @SerializedName("windSpeed")
    var windSpeed: String,
    @SerializedName("windDegree")
    var windDegree: String
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = CURRENT_WEATHER_ID
}
package com.ey.weatherforecastapp.ui.main.model.data


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
)
package com.ey.weatherforecastapp.ui.main.model.web_service

import com.ey.weatherforecastapp.ui.main.model.data.WeatherInfoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("weather")
    fun getWeatherInformation(@Query("id") cityId: Int): Call<WeatherInfoResponse>
}
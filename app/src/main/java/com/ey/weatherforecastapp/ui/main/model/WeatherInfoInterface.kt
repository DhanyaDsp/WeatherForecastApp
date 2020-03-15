package com.ey.weatherforecastapp.ui.main.model

import com.ey.weatherforecastapp.ui.main.model.web_service.RequestListener
import com.ey.weatherforecastapp.ui.main.model.data.WeatherInfoResponse

interface WeatherInfoInterface {
    fun getWeatherInfo(cityId: Int, callback: RequestListener<WeatherInfoResponse>)
    fun getCommonWeatherInfo(cityId: Int, callback: RequestListener<WeatherInfoResponse>)

}
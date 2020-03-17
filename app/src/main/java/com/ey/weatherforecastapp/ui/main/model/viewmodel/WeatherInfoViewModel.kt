package com.ey.weatherforecastapp.ui.main.model.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ey.weatherforecastapp.ui.main.model.WeatherInfoInterface
import com.ey.weatherforecastapp.ui.main.model.data.Weather
import com.ey.weatherforecastapp.ui.main.model.data.WeatherData
import com.ey.weatherforecastapp.ui.main.model.data.WeatherInfoResponse
import com.ey.weatherforecastapp.ui.main.model.web_service.RequestListener
import com.ey.weatherforecastapp.ui.main.utils.kelvinToCelsius
import com.ey.weatherforecastapp.ui.main.utils.unixTimestampToDateTimeString
import com.ey.weatherforecastapp.ui.main.utils.unixTimestampToTimeString

class WeatherInfoViewModel : ViewModel() {
    val weatherListLiveData = MutableLiveData<MutableList<Weather>>()
    val weatherInfoFailureLiveData = MutableLiveData<String>()
    val weatherCommonInfoLiveData = MutableLiveData<WeatherData>()
    val progressBarLiveData = MutableLiveData<Boolean>()
    val weatherInfoLiveData = MutableLiveData<WeatherData>()

    var application: Application? = null

    fun getWeatherInfo(cityId: Int, model: WeatherInfoInterface) {

        progressBarLiveData.postValue(true)
        model.getWeatherInfo(cityId, object : RequestListener<WeatherInfoResponse> {
            override fun onRequestSuccess(data: WeatherInfoResponse) {

                val weatherData = WeatherData(
                    dateTime = data.dt.unixTimestampToDateTimeString(),
                    temperature = data.main.temp.kelvinToCelsius().toString(),
                    cityAndCountry = "${data.name}, ${data.sys.country}",
                    weatherConditionIconUrl = "http://openweathermap.org/img/w/${data.weather[0].icon}.png",
                    weatherConditionIconDescription = data.weather[0].description,
                    humidity = "${data.main.humidity}%",
                    pressure = "${data.main.pressure} mBar",
                    visibility = "${data.visibility / 1000.0} KM",
                    sunrise = data.sys.sunrise.unixTimestampToTimeString(),
                    sunset = data.sys.sunset.unixTimestampToTimeString(),
                    description = data.weather[0].description,
                    windSpeed = data.wind.speed.toString(),
                    windDegree = data.wind.deg.toString()
                )

                progressBarLiveData.postValue(false)
                weatherInfoLiveData.postValue(weatherData)
            }

            override fun onRequestFailed(errorMessage: String) {
                progressBarLiveData.postValue(false)
                weatherInfoFailureLiveData.postValue(errorMessage)
            }
        })
    }


}

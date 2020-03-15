package com.ey.weatherforecastapp.ui.main.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.Toast
import com.ey.weatherforecastapp.ui.main.model.data.WeatherInfoResponse
import com.ey.weatherforecastapp.ui.main.model.web_service.ApiService
import com.ey.weatherforecastapp.ui.main.model.web_service.RequestListener
import com.ey.weatherforecastapp.ui.main.model.web_service.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WeatherInfoImpl(private val context: Context) : WeatherInfoInterface {
    override fun getCommonWeatherInfo(cityId: Int, callback: RequestListener<WeatherInfoResponse>) {
        val apiInterface: ApiService = RetrofitClient.client.create(ApiService::class.java)
        val call: Call<WeatherInfoResponse> = apiInterface.getWeatherInformation(cityId)

        call.enqueue(object : Callback<WeatherInfoResponse> {

            override fun onResponse(
                call: Call<WeatherInfoResponse>,
                response: Response<WeatherInfoResponse>
            ) {
                if (response.body() != null)
                    callback.onRequestSuccess(response.body()!!)
                else
                    callback.onRequestFailed(response.message())
            }

            override fun onFailure(call: Call<WeatherInfoResponse>, t: Throwable) {
                callback.onRequestFailed(t.localizedMessage!!)
            }
        })
    }

    override fun getWeatherInfo(cityId: Int, callback: RequestListener<WeatherInfoResponse>) {

        if (isConnectedToWifi(context)) {
            val apiInterface: ApiService = RetrofitClient.client.create(ApiService::class.java)
            val call: Call<WeatherInfoResponse> = apiInterface.getWeatherInformation(7778677)

            call.enqueue(object : Callback<WeatherInfoResponse> {

                override fun onResponse(
                    call: Call<WeatherInfoResponse>,
                    response: Response<WeatherInfoResponse>
                ) {
                    if (response.body() != null)
                        callback.onRequestSuccess(response.body()!!)
                    else
                        callback.onRequestFailed(response.message())
                }

                override fun onFailure(call: Call<WeatherInfoResponse>, t: Throwable) {
                    callback.onRequestFailed(t.localizedMessage!!)
                }
            })
        } else {
            Toast.makeText(context, "Please Connect your network to wifi ", Toast.LENGTH_LONG)
                .show()
        }

    }

    private fun isConnectedToWifi(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connMgr.allNetworkInfo
        for (i in networkInfo.indices){
            if (networkInfo[i].type == ConnectivityManager.TYPE_WIFI) {
                Log.d("sos","networkInfo ${networkInfo[i].type}")
                return true
            }
        }

        return false

    }
}
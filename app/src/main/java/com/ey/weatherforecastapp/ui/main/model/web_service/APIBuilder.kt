package com.ey.weatherforecastapp.ui.main.model.web_service

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class APIBuilder : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val url = chain.request().url.newBuilder()
            .addQueryParameter("appid", "5ad7218f2e11df834b0eaf3a33a39d2a")
            .build()

        val request = chain.request().newBuilder()
            // .addHeader("Authorization", "5ad7218f2e11df834b0eaf3a33a39d2a")
            .url(url)
            .build()
        Log.d("sos", "url $url")
        Log.d("sos", "request $request")
        return chain.proceed(request)
    }
}
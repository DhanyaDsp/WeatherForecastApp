package com.ey.weatherforecastapp.ui.main.model.web_service

interface RequestListener<T> {
    fun onRequestSuccess(data: T)
    fun onRequestFailed(errorMessage: String)

}
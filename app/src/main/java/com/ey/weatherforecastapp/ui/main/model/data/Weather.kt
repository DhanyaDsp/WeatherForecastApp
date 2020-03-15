package com.ey.weatherforecastapp.ui.main.model.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Weather(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("main")
    val main: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("icon")
    val icon: String = ""

) : Serializable
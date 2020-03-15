package com.ey.weatherforecastapp.ui.main.model.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class CommonWeatherDetails(
    @SerializedName("timezone")
    val timezone: Int = 0,
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("cod")
    val cod: String = "",
    @SerializedName("dt")
    val dt: String = "",
    @SerializedName("visibility")
    val visibility: String = "",
    @SerializedName("base")
    val base: String = ""

) : Serializable
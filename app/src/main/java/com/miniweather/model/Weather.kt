package com.miniweather.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("weather")
    val weatherList: List<Condition>,
    @SerializedName("main")
    val temp: Temperature,
    @SerializedName("wind")
    val wind: Wind,
    @SerializedName("name")
    val location: String
)

data class Condition(
    @SerializedName("main")
    val condition: String,
    @SerializedName("icon")
    val icon: String
)

data class Temperature(
    @SerializedName("temp")
    val value: Double
)

data class Wind(
    @SerializedName("speed")
    val speed: Double,
    @SerializedName("deg")
    val direction: Double
)

data class Weather(
    val condition: String,
    val temperature: Int,
    val windSpeed: Int,
    val windDirection: String,
    val location: String,
    val iconUrl: String
)

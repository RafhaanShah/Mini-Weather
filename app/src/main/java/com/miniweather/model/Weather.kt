package com.miniweather.model

import androidx.room.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    @SerialName("weather")
    val weatherList: List<Condition>,
    @SerialName("main")
    val temp: Temperature,
    @SerialName("wind")
    val wind: Wind,
    @SerialName("name")
    val location: String
)

@Serializable
data class Condition(
    @SerialName("main")
    val condition: String,
    @SerialName("icon")
    val icon: String
)

@Serializable
data class Temperature(
    @SerialName("temp")
    val value: Double
)

@Serializable
data class Wind(
    @SerialName("speed")
    val speed: Double,
    @SerialName("deg")
    val direction: Double
)

@Entity(primaryKeys= [ "latitude", "longitude" ])
data class Weather(
    val condition: String,
    val temperature: Int,
    val windSpeed: Int,
    val windDirection: String,
    val location: String,
    val iconUrl: String,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double
)

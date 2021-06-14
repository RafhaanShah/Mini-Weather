package com.miniweather.model

import androidx.room.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val weather: List<Condition>,
    @SerialName("main")
    val temperature: Temperature,
    val wind: Wind,
    @SerialName("name")
    val location: String
) {
    @Serializable
    data class Condition(
        @SerialName("main")
        val value: String,
        val icon: String
    )

    @Serializable
    data class Temperature(
        @SerialName("temp")
        val value: Double
    )

    @Serializable
    data class Wind(
        val speed: Double,
        val deg: Double
    )
}

@Entity(primaryKeys = ["latitude", "longitude"])
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

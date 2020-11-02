package com.miniweather.testutil

import com.miniweather.BuildConfig
import com.miniweather.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val fakeTimestamp: Long = 1587362940000L

const val fakeError = "Something went wrong"

val fakeLocation = Location(51.0, 0.0)

val fakeWeatherResponse = WeatherResponse(
    weatherList = listOf(Condition("Sunny", "01d")),
    temp = Temperature(25.0),
    wind = Wind(10.0, 75.0),
    location = "London, UK"
)

val fakeCardinalDirections = arrayOf(
    "North",
    "North East",
    "East",
    "South East",
    "South",
    "South West",
    "West",
    "North West",
    "North"
)

val fakeWeather = Weather(
    fakeWeatherResponse.weatherList.first().condition,
    fakeWeatherResponse.temp.value.toInt(),
    fakeWeatherResponse.wind.speed.toInt(),
    fakeCardinalDirections[2],
    fakeWeatherResponse.location,
    BuildConfig.IMAGE_BASE_URL + fakeWeatherResponse.weatherList.first().icon + ".png",
    fakeTimestamp,
    fakeLocation.latitude,
    fakeLocation.longitude,
)

val fakeWeatherResponseJson by lazy { Json.encodeToString(fakeWeatherResponse) }

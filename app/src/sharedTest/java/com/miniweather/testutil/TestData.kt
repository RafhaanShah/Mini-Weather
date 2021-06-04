package com.miniweather.testutil

import com.miniweather.model.*
import com.miniweather.service.network.PNG

const val fakeTimestamp: Long = 1623753000000L // 15-06-2021 10:30:00 AM GMT

const val fakeError = "Something went wrong"

const val imageAssets = "file:///android_asset/images/"

val fakeLocation = Location(51.51, -0.13) // London, Leicester Square

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
     imageAssets + fakeWeatherResponse.weatherList.first().icon + PNG,
    fakeTimestamp,
    fakeLocation.latitude,
    fakeLocation.longitude,
)

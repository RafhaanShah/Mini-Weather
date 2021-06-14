package com.miniweather.testutil

import com.miniweather.mapper.PNG
import com.miniweather.model.Location
import com.miniweather.model.Weather
import com.miniweather.model.WeatherResponse
import kotlin.math.roundToInt

const val fakeTimestamp: Long = 1623753000000L // 15-06-2021 10:30:00 AM GMT

const val fakeError = "Something went wrong"

const val imageAssets = "file:///android_asset/images/"

val fakeLocation = Location(51.51, -0.13) // London, Leicester Square

val fakeWeatherResponse = WeatherResponse(
    weather = listOf(WeatherResponse.Condition("Sunny", "01d")),
    temperature = WeatherResponse.Temperature(25.0),
    wind = WeatherResponse.Wind(1.5, 75.0),
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
    fakeWeatherResponse.weather.first().value,
    fakeWeatherResponse.temperature.value.roundToInt(),
    fakeWeatherResponse.wind.speed.roundToInt(),
    fakeCardinalDirections[2],
    fakeWeatherResponse.location,
    imageAssets + fakeWeatherResponse.weather.first().icon + PNG,
    fakeTimestamp,
    fakeLocation.latitude,
    fakeLocation.longitude,
)

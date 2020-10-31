package com.miniweather.testutil

import com.miniweather.model.*

val fakeWeather = Weather(
    "Sunny",
    22,
    17,
    "East",
    "London, UK",
    "https://weather.icon/01m",
    1000,
    1.11,
    2.22,
)

val fakeWeatherResponse = WeatherResponse(
    weatherList = listOf(
        Condition(
            "Sunny",
            "https://weather.icon/"
        )
    ),
    temp = Temperature(22.toDouble()),
    wind = Wind(17.toDouble(), 70.0),
    location = "London, UK"
)

val fakeLocation = Location(1.111, 2.222)

val fakeLocationRounded = Location(1.11, 2.22)

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

const val fakeTimestamp: Long = 1000L

const val fakeError = "Something went wrong"

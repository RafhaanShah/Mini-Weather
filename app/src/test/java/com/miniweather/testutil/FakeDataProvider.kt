package com.miniweather.testutil

import com.miniweather.model.*

class FakeDataProvider {

    companion object {
        fun provideFakeWeather(): Weather = Weather(
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

        fun provideFakeWeatherResponse(): WeatherResponse = WeatherResponse(
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

        fun provideFakeLocation(): Location = Location(1.111, 2.222)

        fun provideFakeLocationRounded(): Location = Location(1.11, 2.22)

        fun provideFakeCardinalDirections() = arrayOf(
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
    }

}

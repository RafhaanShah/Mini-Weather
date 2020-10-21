package com.miniweather.testutil

import com.miniweather.model.Weather

class FakeDataProvider {

    companion object {
        fun provideFakeWeather(): Weather = Weather(
            "Sunny",
            69,
            42,
            "North",
            "London, UK",
            "https://weather.icon/01m",
            1000,
            1.111,
            2.222,
        )
    }

}

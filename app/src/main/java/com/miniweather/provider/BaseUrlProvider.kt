package com.miniweather.provider

import javax.inject.Inject

class BaseUrlProvider @Inject constructor() {

    fun getBaseWeatherUrl(): String {
        return "http://api.openweathermap.org/"
    }

    fun getBaseImageUrl(): String {
        return "https://openweathermap.org/img/wn/"
    }

}

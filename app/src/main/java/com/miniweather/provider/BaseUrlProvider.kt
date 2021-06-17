package com.miniweather.provider

import com.miniweather.BuildConfig
import javax.inject.Inject

class BaseUrlProvider @Inject constructor() {

    val weatherApi: String = BuildConfig.WEATHER_API_BASE_URL

    val weatherImage: String = BuildConfig.WEATHER_IMAGE_BASE_URL
}

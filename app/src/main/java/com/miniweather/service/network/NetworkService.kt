package com.miniweather.service.network

import com.miniweather.repository.api.WeatherApi
import javax.inject.Inject

// IO Dispatcher is not needed for Retrofit
// https://www.lukaslechner.com/do-i-need-to-call-suspend-functions-of-retrofit-and-room-on-a-background-thread/
class NetworkService @Inject constructor(
    private val weatherApi: WeatherApi,
) {

    suspend fun <T> call(apiFunc: suspend WeatherApi.() -> T): Result<T> =
        runCatching { apiFunc(weatherApi) }
}

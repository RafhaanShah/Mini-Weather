package com.miniweather.service.network

import com.miniweather.BuildConfig
import com.miniweather.app.BaseUrlProvider
import com.miniweather.model.Location
import com.miniweather.model.WeatherResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class NetworkService @Inject constructor(
    private val baseUrlProvider: BaseUrlProvider,
    private val weatherApi: WeatherApi,
    @Named("IO") private val dispatcher: CoroutineDispatcher
) {

    suspend fun getWeather(location: Location): Result<WeatherResponse> = makeRequest {
        weatherApi.getWeather(
            baseUrlProvider.getBaseWeatherUrl() + weatherPath,
            location.latitude,
            location.longitude
        )
    }

    private suspend fun <T> makeRequest(request: suspend () -> T): Result<T> =
        withContext(dispatcher) {
            try {
                Result.success(request())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

}

const val weatherPath = "data/2.5/weather?units=metric&appid=" + BuildConfig.API_KEY

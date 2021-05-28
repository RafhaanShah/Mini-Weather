package com.miniweather.service.network

import com.miniweather.model.Location
import com.miniweather.model.WeatherResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class NetworkService @Inject constructor(
    private val apiService: WeatherApi,
    @Named("IO") private val dispatcher: CoroutineDispatcher
) {

    suspend fun getWeather(location: Location): Result<WeatherResponse> = makeRequest {
        apiService.getWeather(location.latitude, location.longitude)
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

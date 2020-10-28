package com.miniweather.service.network

import com.miniweather.model.DataResult
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

    suspend fun getWeather(location: Location): DataResult<WeatherResponse> = makeRequest {
        try {
            DataResult.Success(apiService.getWeather(location.latitude, location.longitude))
        } catch (e: Exception) {
            DataResult.Failure(e)
        }
    }

    private suspend fun <T> makeRequest(query: suspend () -> T): T = withContext(dispatcher) { query() }

}

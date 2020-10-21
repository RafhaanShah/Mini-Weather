package com.miniweather.service.network

import com.miniweather.model.DataResult
import com.miniweather.model.WeatherResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NetworkService @Inject constructor(
    private val apiService: WeatherApi,
    private val dispatcher: CoroutineDispatcher
) {

    suspend fun getWeather(lat: Double, lon: Double): DataResult<WeatherResponse> = makeRequest {
        try {
            DataResult.Success(apiService.getWeather(lat, lon))
        } catch (e: Exception) {
            DataResult.Failure(e)
        }
    }

    private suspend fun <T> makeRequest(query: suspend () -> T): T = withContext(dispatcher) { query() }

}

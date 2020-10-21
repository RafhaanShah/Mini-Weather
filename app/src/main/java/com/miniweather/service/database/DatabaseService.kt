package com.miniweather.service.database

import com.miniweather.model.Weather
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DatabaseService @Inject constructor(
    private val weatherDao: WeatherDao,
    private val dispatcher: CoroutineDispatcher
) {

    suspend fun getCachedData(lat: Double, lon: Double, maxAge: Long): List<Weather> =
        execute { weatherDao.getCachedData(lat, lon, maxAge) }

    suspend fun insertIntoCache(weather: Weather) = execute { weatherDao.insertIntoCache(weather) }

    suspend fun deleteInvalidCaches(maxAge: Long) = execute { weatherDao.deleteInvalidCaches(maxAge) }

    private suspend fun <T> execute(query: suspend () -> T): T = withContext(dispatcher) { query() }

}

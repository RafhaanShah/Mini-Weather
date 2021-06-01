package com.miniweather.service.database

import com.miniweather.model.Location
import com.miniweather.model.Weather
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class DatabaseService @Inject constructor(
    private val weatherDao: WeatherDao,
    @Named("IO") private val dispatcher: CoroutineDispatcher
) {

    suspend fun getCachedData(location: Location, maxAge: Long): List<Weather> =
        execute { weatherDao.getCachedData(location.latitude, location.longitude, maxAge) }

    suspend fun insertIntoCache(weather: Weather) = execute { weatherDao.insertIntoCache(weather) }

    suspend fun deleteInvalidCaches(maxAge: Long) =
        execute { weatherDao.deleteInvalidCaches(maxAge) }

    private suspend fun <T> execute(query: suspend () -> T): T = withContext(dispatcher) { query() }

}

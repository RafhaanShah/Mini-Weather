package com.miniweather.repository

import com.miniweather.mapper.WeatherResponseMapper
import com.miniweather.model.Location
import com.miniweather.model.Weather
import com.miniweather.provider.DateTimeProvider
import com.miniweather.service.database.DatabaseService
import com.miniweather.service.network.NetworkService
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.round

class WeatherRepository @Inject constructor(
    private val databaseService: DatabaseService,
    private val dateTimeProvider: DateTimeProvider,
    private val networkService: NetworkService,
    private val weatherResponseMapper: WeatherResponseMapper,
) {

    suspend fun getWeather(location: Location): Result<Weather> {
        val roundedLocation = roundLocation(location)
        return networkService.call { getWeather(location.latitude, location.longitude) }.fold(
            { response ->
                val weather = weatherResponseMapper.map(response, roundedLocation)
                updateDb(weather)
                Result.success(weather)
            }, { error ->
                getCachedData(roundedLocation, error)
            }
        )
    }

    private suspend fun updateDb(weather: Weather) {
        coroutineScope {
            launch {
                databaseService.execute { deleteInvalidCaches(dateTimeProvider.getCurrentTime() - maxCacheAge) }
                databaseService.execute { insertIntoCache(weather) }
            }
        }
    }

    private suspend fun getCachedData(
        roundedLocation: Location,
        error: Throwable
    ): Result<Weather> =
        databaseService.execute {
            getCachedData(
                roundedLocation.latitude, roundedLocation.longitude,
                dateTimeProvider.getCurrentTime() - maxCacheAge
            )
        }.fold({ Result.success(it) }, { Result.failure(error) })


    // Round to 2 decimal places which is roughly 1KM at the equator
    private fun roundLocation(location: Location) =
        Location(
            round(location.latitude * 100.0) / 100.0,
            round(location.longitude * 100.0) / 100.0
        )

}

internal val maxCacheAge = TimeUnit.DAYS.toMillis(1)

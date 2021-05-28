package com.miniweather.service.weather

import com.miniweather.BuildConfig
import com.miniweather.R
import com.miniweather.model.Location
import com.miniweather.model.Weather
import com.miniweather.model.WeatherResponse
import com.miniweather.service.database.DatabaseService
import com.miniweather.service.network.NetworkService
import com.miniweather.service.util.StringResourceService
import com.miniweather.service.util.TimeService
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.roundToInt

class WeatherService @Inject constructor(
    private val networkService: NetworkService,
    private val timeService: TimeService,
    private val databaseService: DatabaseService,
    private val stringResourceService: StringResourceService
) {
    companion object {
        val CACHE_MAX_AGE = TimeUnit.DAYS.toMillis(1)
    }

    suspend fun getWeather(location: Location): Result<Weather> {
        val roundedLocation =
            Location(roundCoordinate(location.latitude), roundCoordinate(location.longitude))

        return networkService.getWeather(location).fold(
            {
                weatherSuccess(roundedLocation, it)
            }, {
                weatherFailure(roundedLocation, it)
            }
        )
    }

    private suspend fun weatherSuccess(
        location: Location,
        networkResponse: WeatherResponse
    ): Result<Weather> = coroutineScope {
        val weather = createWeatherData(networkResponse, location.latitude, location.longitude)
        launch {
            databaseService.deleteInvalidCaches(timeService.getCurrentTime() - CACHE_MAX_AGE)
            databaseService.insertIntoCache(weather)
        }
        Result.success(weather)
    }


    private suspend fun weatherFailure(
        roundedLocation: Location,
        exception: Throwable
    ): Result<Weather> {
        val cache = databaseService.getCachedData(
            roundedLocation,
            timeService.getCurrentTime() - CACHE_MAX_AGE
        )
        return if (cache.isNotEmpty()) {
            Result.success(cache.first())
        } else {
            Result.failure(exception)
        }
    }

    private fun createWeatherData(
        weatherResponse: WeatherResponse,
        lat: Double,
        lon: Double
    ): Weather {
        return Weather(
            weatherResponse.weatherList.firstOrNull()?.condition
                ?: stringResourceService.getString(R.string.unknown),
            weatherResponse.temp.value.roundToInt(),
            weatherResponse.wind.speed.roundToInt(),
            formatBearing(weatherResponse.wind.direction),
            weatherResponse.location,
            BuildConfig.IMAGE_BASE_URL + (weatherResponse.weatherList.firstOrNull()?.icon) + ".png",
            timeService.getCurrentTime(),
            lat,
            lon
        )
    }

    // Round to 2 decimal places which is roughly 1KM at the equator
    private fun roundCoordinate(coordinate: Double): Double {
        return round(coordinate * 100.0) / 100.0
    }

    private fun formatBearing(bearing: Double): String {
        if (bearing < 0 || bearing > 360) {
            return stringResourceService.getString(
                R.string.unknown
            )
        }
        val directions = stringResourceService.getStringArray(R.array.directions)
        val index = floor(((bearing - 22.5) % 360) / 45).toInt()
        return directions[index + 1]
    }

}

package com.miniweather.service

import com.miniweather.BuildConfig
import com.miniweather.R
import com.miniweather.model.DataResult
import com.miniweather.model.Weather
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.roundToInt

class WeatherService @Inject constructor(
    private val networkService: NetworkService,
    private val stringResourceService: StringResourceService
) {

    fun getWeather(
        lat: Double,
        lon: Double,
        callback: (DataResult<Weather>) -> Unit
    ) {
        networkService.makeWeatherRequest(lat, lon) {
            when (it) {
                is DataResult.Success -> {
                    val weatherResponse = it.data
                    callback.invoke(
                        DataResult.Success(
                            Weather(
                                weatherResponse.weatherList.firstOrNull()?.condition
                                    ?: stringResourceService.getString(R.string.unknown),
                                weatherResponse.temp.value.roundToInt(),
                                weatherResponse.wind.speed.roundToInt(),
                                formatBearing(weatherResponse.wind.direction),
                                weatherResponse.location,
                                BuildConfig.IMAGE_BASE_URL +
                                        (weatherResponse.weatherList.firstOrNull()?.icon) + ".png"
                            )
                        )
                    )
                }

                is DataResult.Failure -> {
                    callback.invoke(DataResult.Failure(it.exception))
                }
            }
        }
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

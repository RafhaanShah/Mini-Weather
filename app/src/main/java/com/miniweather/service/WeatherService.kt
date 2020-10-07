package com.miniweather.service

import com.miniweather.BuildConfig
import com.miniweather.model.Weather
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.roundToInt

class WeatherService @Inject constructor(private val networkService: NetworkService) {

    fun getWeather(
        lat: Double,
        lon: Double,
        callback: (weather: Weather?, success: Boolean) -> Unit
    ) {
        networkService.makeWeatherRequest(lat, lon) { resp, success ->
            if (success) {
                callback.invoke(
                    Weather(
                        resp?.weatherList?.firstOrNull()?.condition ?: "",
                        resp?.temp?.value?.roundToInt() ?: 0,
                        resp?.wind?.speed?.roundToInt() ?: 0,
                        formatBearing(resp?.wind?.direction ?: -1.0),
                        resp?.location ?: "",
                        BuildConfig.IMAGE_BASE_URL + "/img/wn/" + (resp?.weatherList?.firstOrNull()?.icon
                            ?: "") + ".png"
                    ), true
                )
            } else {
                callback.invoke(null, false)
            }
        }
    }

    private fun formatBearing(bearing: Double): String {
        if (bearing < 0 || bearing > 360) {
            return "Unknown"
        }
        val directions = arrayOf(
            "North",
            "North East",
            "East",
            "South East",
            "South",
            "South West",
            "West",
            "North West",
            "North"
        )
        val index = floor(((bearing - 22.5) % 360) / 45).toInt()
        return directions[index + 1]
    }
}

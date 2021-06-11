package com.miniweather.mapper

import com.miniweather.R
import com.miniweather.model.Location
import com.miniweather.model.Weather
import com.miniweather.model.WeatherResponse
import com.miniweather.provider.BaseUrlProvider
import com.miniweather.provider.DateTimeProvider
import com.miniweather.provider.ResourceProvider
import javax.inject.Inject
import kotlin.math.floor
import kotlin.math.roundToInt

class WeatherResponseMapper @Inject constructor(
    private val baseUrlProvider: BaseUrlProvider,
    private val dateTimeProvider: DateTimeProvider,
    private val resourceProvider: ResourceProvider
) {

    fun map(response: WeatherResponse, location: Location) =
        Weather(
            response.weather.first().value,
            response.temperature.value.roundToInt(),
            response.wind.speed.roundToInt(),
            formatBearing(response.wind.deg),
            response.location,
            baseUrlProvider.getBaseImageUrl() + response.weather.first().icon + PNG,
            dateTimeProvider.getCurrentTime(),
            location.latitude,
            location.longitude
        )

    private fun formatBearing(bearing: Double): String {
        if (bearing < 0 || bearing > 360)
            return resourceProvider.getString(R.string.unknown)

        val directions = resourceProvider.getStringArray(R.array.directions)
        val index = floor(((bearing - 22.5) % 360) / 45).toInt()
        return directions[index + 1]
    }

}

internal const val PNG = ".png"

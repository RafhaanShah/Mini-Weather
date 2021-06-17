package com.miniweather.mapper

import com.miniweather.R
import com.miniweather.model.Location
import com.miniweather.model.Weather
import com.miniweather.model.WeatherResponse
import com.miniweather.provider.BaseUrlProvider
import com.miniweather.provider.DateTimeProvider
import com.miniweather.provider.ResourceProvider
import javax.inject.Inject
import kotlin.math.roundToInt

internal const val PNG = ".png"

class WeatherResponseMapper @Inject constructor(
    private val baseUrlProvider: BaseUrlProvider,
    private val dateTimeProvider: DateTimeProvider,
    private val resourceProvider: ResourceProvider
) {

    fun map(response: WeatherResponse, location: Location): Weather =
        Weather(
            response.weather.first().value,
            response.temperature.value.roundToInt(),
            response.wind.speed.roundToInt(),
            formatBearing(response.wind.deg),
            response.location,
            formatImageUrl(response.weather.first().icon),
            dateTimeProvider.getCurrentTime(),
            location.latitude,
            location.longitude
        )

    private fun formatBearing(bearing: Double): String =
        resourceProvider.getStringArray(R.array.directions)[CardinalDirectionMapper.map(bearing)]

    private fun formatImageUrl(icon: String): String =
        baseUrlProvider.weatherImage + icon + PNG
}

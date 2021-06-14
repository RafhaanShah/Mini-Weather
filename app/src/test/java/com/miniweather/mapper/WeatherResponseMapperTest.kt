package com.miniweather.mapper

import com.google.common.truth.Truth.assertThat
import com.miniweather.R
import com.miniweather.provider.BaseUrlProvider
import com.miniweather.provider.DateTimeProvider
import com.miniweather.provider.ResourceProvider
import com.miniweather.testutil.*
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

class WeatherResponseMapperTest : BaseTest() {

    @MockK
    private lateinit var baseUrlProvider: BaseUrlProvider

    @MockK
    private lateinit var dateTimeProvider: DateTimeProvider

    @MockK
    private lateinit var resourceProvider: ResourceProvider

    private lateinit var weatherResponseMapper: WeatherResponseMapper

    @Before
    fun setup() {
        every { baseUrlProvider.getBaseImageUrl() } returns imageAssets
        every { dateTimeProvider.getCurrentTime() } returns fakeTimestamp
        every { resourceProvider.getStringArray(R.array.directions) } returns fakeCardinalDirections

        weatherResponseMapper = WeatherResponseMapper(
            baseUrlProvider, dateTimeProvider, resourceProvider
        )
    }

    @Test
    fun whenMap_returnsMappedWeather() {
        val actual = weatherResponseMapper.map(fakeWeatherResponse, fakeLocation)
        assertThat(actual).isEqualTo(fakeWeather)
    }

}

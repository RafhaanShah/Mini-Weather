package com.miniweather.mapper

import com.miniweather.R
import com.miniweather.provider.BaseUrlProvider
import com.miniweather.provider.DateTimeProvider
import com.miniweather.provider.ResourceProvider
import com.miniweather.testutil.*
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

class WeatherResponseMapperTest : BaseTest() {

    @Mock
    private lateinit var baseUrlProvider: BaseUrlProvider

    @Mock
    private lateinit var dateTimeProvider: DateTimeProvider

    @Mock
    private lateinit var resourceProvider: ResourceProvider

    private lateinit var weatherResponseMapper: WeatherResponseMapper

    @Before
    fun setup() {
        whenever(baseUrlProvider.getBaseImageUrl()).thenReturn(imageAssets)
        whenever(dateTimeProvider.getCurrentTime()).thenReturn(fakeTimestamp)
        whenever(resourceProvider.getStringArray(R.array.directions)).thenReturn(
            fakeCardinalDirections
        )

        weatherResponseMapper = WeatherResponseMapper(
            baseUrlProvider, dateTimeProvider, resourceProvider
        )
    }

    @Test
    fun whenMap_returnsMappedWeather() {
        assertEquals(fakeWeather, weatherResponseMapper.map(fakeWeatherResponse, fakeLocation))
    }

}

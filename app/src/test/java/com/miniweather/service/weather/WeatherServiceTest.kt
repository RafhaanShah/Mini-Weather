package com.miniweather.service.weather

import com.miniweather.model.DataResult
import com.miniweather.service.database.DatabaseService
import com.miniweather.service.network.NetworkService
import com.miniweather.service.util.StringResourceService
import com.miniweather.service.util.TimeService
import com.miniweather.testutil.*
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

@ExperimentalCoroutinesApi
class WeatherServiceTest : BaseTest() {

    @Mock
    private lateinit var mockNetworkService: NetworkService
    @Mock
    private lateinit var mockTimeService: TimeService
    @Mock
    private lateinit var mockDatabaseService: DatabaseService
    @Mock
    private lateinit var mockStringResourceService: StringResourceService

    private lateinit var weatherService: WeatherService

    @Before
    fun setup() {
        weatherService =
            WeatherService(mockNetworkService, mockTimeService, mockDatabaseService, mockStringResourceService)

        whenever(mockTimeService.getCurrentTime()).thenReturn(fakeTimestamp)
    }

    @Test
    fun whenGetWeather_andNetworkServiceSucceeds_returnsWeather_andCaches() = runBlockingTest {
        whenever(mockStringResourceService.getStringArray(any())).thenReturn(fakeCardinalDirections)
        whenever(mockNetworkService.getWeather(any())).thenReturn(DataResult.Success(fakeWeatherResponse))

        val actual = weatherService.getWeather(fakeLocation)

        verify(mockNetworkService).getWeather(fakeLocation)

        val weather = (actual as DataResult.Success).data
        assertTrue(weather.iconUrl.contains(fakeWeatherResponse.weatherList.first().icon))
        assertEquals(fakeWeatherResponse.weatherList.first().condition, weather.condition)
        assertEquals(fakeWeatherResponse.temp.value.toInt(), weather.temperature)
        assertEquals(fakeWeatherResponse.wind.speed.toInt(), weather.windSpeed)
        assertEquals(fakeCardinalDirections[2], weather.windDirection)
        assertEquals(fakeWeatherResponse.location, weather.location)
        assertEquals(fakeLocationRounded.latitude, weather.latitude, 0.0)
        assertEquals(fakeLocationRounded.longitude, weather.longitude, 0.0)

        verify(mockDatabaseService).deleteInvalidCaches(fakeTimestamp - WeatherService.CACHE_MAX_AGE)
        verify(mockDatabaseService).insertIntoCache(weather)
    }

    @Test
    fun whenGetWeather_andNetworkServiceFails_andValidCacheExists_returnsWeather() = runBlockingTest {
        whenever(mockNetworkService.getWeather(any()))
            .thenReturn(DataResult.Failure(Exception(fakeError)))
        whenever(mockDatabaseService.getCachedData(any(), any()))
            .thenReturn(listOf(fakeWeather))

        val actual = weatherService.getWeather(fakeLocation)

        verify(mockNetworkService).getWeather(fakeLocation)
        verify(mockDatabaseService).getCachedData(
            fakeLocationRounded,
            fakeTimestamp - WeatherService.CACHE_MAX_AGE
        )

        val weather = (actual as DataResult.Success).data
        assertTrue(weather.iconUrl.contains(fakeWeatherResponse.weatherList.first().icon))
        assertEquals(fakeWeatherResponse.weatherList.first().condition, weather.condition)
        assertEquals(fakeWeatherResponse.temp.value.toInt(), weather.temperature)
        assertEquals(fakeWeatherResponse.wind.speed.toInt(), weather.windSpeed)
        assertEquals(fakeCardinalDirections[2], weather.windDirection)
        assertEquals(fakeWeatherResponse.location, weather.location)
        assertEquals(fakeWeather.latitude, weather.latitude, 0.0)
        assertEquals(fakeWeather.longitude, weather.longitude, 0.0)

        verifyNoMoreInteractions(mockDatabaseService)
    }

    @Test
    fun whenGetWeather_andNetworkServiceFails_andNoValidCache_returnsFailure() = runBlockingTest {
        whenever(mockNetworkService.getWeather(any()))
            .thenReturn(DataResult.Failure(Exception(fakeError)))
        whenever(mockDatabaseService.getCachedData(any(), any()))
            .thenReturn(emptyList())

        val actual = weatherService.getWeather(fakeLocation)

        verify(mockNetworkService).getWeather(fakeLocation)

        assertTrue(actual is DataResult.Failure)
    }

}

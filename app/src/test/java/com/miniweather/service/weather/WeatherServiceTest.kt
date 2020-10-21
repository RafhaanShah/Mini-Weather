package com.miniweather.service.weather

import com.miniweather.model.*
import com.miniweather.service.database.DatabaseService
import com.miniweather.service.network.NetworkService
import com.miniweather.service.util.StringResourceService
import com.miniweather.service.util.TimeService
import com.miniweather.testutil.FakeDataProvider
import com.nhaarman.mockitokotlin2.*
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class WeatherServiceTest {

    @Mock
    private lateinit var mockNetworkService: NetworkService

    @Mock
    private lateinit var mockTimeService: TimeService

    @Mock
    private lateinit var mockDatabaseService: DatabaseService

    @Mock
    private lateinit var mockStringResourceService: StringResourceService

    private lateinit var weatherService: WeatherService

    private val fakeLat = 1.111
    private val fakeLon = 2.222
    private val fakeLatRounded = 1.11
    private val fakeLonRounded = 2.22
    private val fakeTimestamp = 1000L
    private val fakeWeather = FakeDataProvider.provideFakeWeather()
    private val fakeWeatherResponse = WeatherResponse(
        weatherList = listOf(
            Condition(
                fakeWeather.condition,
                fakeWeather.iconUrl.substring(fakeWeather.iconUrl.length - 4)
            )
        ),
        temp = Temperature(fakeWeather.temperature.toDouble()),
        wind = Wind(fakeWeather.windSpeed.toDouble(), 70.0),
        location = fakeWeather.location
    )

    @Before
    fun setup() {
        weatherService =
            WeatherService(mockNetworkService, mockTimeService, mockDatabaseService, mockStringResourceService)

        whenever(mockTimeService.getCurrentTime()).thenReturn(fakeTimestamp)
        whenever(mockStringResourceService.getStringArray(any())).thenReturn(
            arrayOf(
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
        )
    }

    @Test
    fun whenGetWeather_andNetworkServiceSucceeds_returnsWeather_andCaches() = runBlockingTest {
        whenever(mockNetworkService.getWeather(any(), any()))
            .thenReturn(DataResult.Success(fakeWeatherResponse))

        val result = weatherService.getWeather(fakeLat, fakeLon)

        verify(mockNetworkService).getWeather(eq(fakeLat), eq(fakeLon))

        val weather = (result as DataResult.Success).data
        assertTrue(weather.iconUrl.contains(fakeWeatherResponse.weatherList.first().icon))
        assertEquals(fakeWeatherResponse.weatherList.first().condition, weather.condition)
        assertEquals(fakeWeatherResponse.temp.value.toInt(), weather.temperature)
        assertEquals(fakeWeatherResponse.wind.speed.toInt(), weather.windSpeed)
        assertEquals("East", weather.windDirection)
        assertEquals(fakeWeatherResponse.location, weather.location)
        assertEquals(fakeLatRounded, weather.latitude, 0.0)
        assertEquals(fakeLonRounded, weather.longitude, 0.0)

        verify(mockDatabaseService).insertIntoCache(weather)
        verify(mockDatabaseService).deleteInvalidCaches(fakeTimestamp - WeatherService.CACHE_MAX_AGE)
    }

    @Test
    fun whenGetWeather_andNetworkServiceFails_andValidCacheExists_returnsWeather() = runBlockingTest {
        whenever(mockNetworkService.getWeather(any(), any()))
            .thenReturn(DataResult.Failure(Exception("Something went wrong")))
        whenever(mockDatabaseService.getCachedData(any(), any(), any()))
            .thenReturn(listOf(fakeWeather))

        val result = weatherService.getWeather(fakeLat, fakeLon)

        verify(mockNetworkService).getWeather(eq(fakeLat), eq(fakeLon))
        verify(mockDatabaseService).getCachedData(
            fakeLatRounded,
            fakeLonRounded,
            fakeTimestamp - WeatherService.CACHE_MAX_AGE
        )

        val weather = (result as DataResult.Success).data
        assertTrue(weather.iconUrl.contains(fakeWeatherResponse.weatherList.first().icon))
        assertEquals(fakeWeatherResponse.weatherList.first().condition, weather.condition)
        assertEquals(fakeWeatherResponse.temp.value.toInt(), weather.temperature)
        assertEquals(fakeWeatherResponse.wind.speed.toInt(), weather.windSpeed)
        assertEquals("North", weather.windDirection)
        assertEquals(fakeWeatherResponse.location, weather.location)
        assertEquals(fakeWeather.latitude, weather.latitude, 0.0)
        assertEquals(fakeWeather.longitude, weather.longitude, 0.0)

        verifyNoMoreInteractions(mockDatabaseService)
    }

    @Test
    fun whenGetWeather_andNetworkServiceFails_andNoValidCache_returnsFailure() = runBlockingTest {
        whenever(mockNetworkService.getWeather(any(), any()))
            .thenReturn(DataResult.Failure(Exception("Something went wrong")))
        whenever(mockDatabaseService.getCachedData(any(), any(), any()))
            .thenReturn(emptyList())

        val result = weatherService.getWeather(fakeLat, fakeLon)

        verify(mockNetworkService).getWeather(eq(fakeLat), eq(fakeLon))

        TestCase.assertTrue(result is DataResult.Failure)
    }

}

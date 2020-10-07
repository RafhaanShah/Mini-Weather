package com.miniweather.service

import com.miniweather.model.*
import com.nhaarman.mockitokotlin2.*
import junit.framework.TestCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WeatherServiceTest {

    @Mock
    private lateinit var mockNetworkService: NetworkService

    @Mock
    private lateinit var mockStringResourceService: StringResourceService

    private lateinit var weatherService: WeatherService

    private val fakeLat = 1.1
    private val fakeLon = 2.2
    private val fakeWeatherResponse = WeatherResponse(
        weatherList = listOf(Condition("Clear", "01m")),
        temp = Temperature(42.0),
        wind = Wind(23.0, 70.0),
        location = "London, UK"
    )

    @Before
    fun setup() {
        weatherService = WeatherService(mockNetworkService, mockStringResourceService)
        whenever(mockStringResourceService.getStringArray(any())).thenReturn(
            arrayOf(
                "a",
                "b",
                "c",
                "d",
                "e",
                "f",
                "g",
                "h"
            )
        )
    }

    @Test
    fun whenGetWeather_andNetworkServiceSucceeds_returnsWeather() {
        val weatherCaptor = argumentCaptor<(DataResult<WeatherResponse>) -> Unit>()
        var result: DataResult<Weather>? = null

        weatherService.getWeather(fakeLat, fakeLon) {
            result = it
        }

        verify(mockNetworkService).makeWeatherRequest(eq(fakeLat), eq(fakeLon), weatherCaptor.capture())
        weatherCaptor.firstValue.invoke(DataResult.Success(fakeWeatherResponse))

        val weather = (result as DataResult.Success).data
        assertTrue(weather.iconUrl.contains(fakeWeatherResponse.weatherList.first().icon))
        assertEquals(fakeWeatherResponse.weatherList.first().condition, weather.condition)
        assertEquals(fakeWeatherResponse.temp.value.toInt(), weather.temperature)
        assertEquals(fakeWeatherResponse.wind.speed.toInt(), weather.windSpeed)
        assertEquals("c", weather.windDirection)
        assertEquals(fakeWeatherResponse.location, weather.location)
    }

    @Test
    fun whenGetWeather_andNetworkServiceFails_returnsFailure() {
        val weatherCaptor = argumentCaptor<(DataResult<WeatherResponse>) -> Unit>()
        var result: DataResult<Weather>? = null

        weatherService.getWeather(fakeLat, fakeLon) {
            result = it
        }

        verify(mockNetworkService).makeWeatherRequest(eq(fakeLat), eq(fakeLon), weatherCaptor.capture())
        weatherCaptor.firstValue.invoke(DataResult.Failure(Exception("Some Error")))

        TestCase.assertTrue(result is DataResult.Failure)
    }

}

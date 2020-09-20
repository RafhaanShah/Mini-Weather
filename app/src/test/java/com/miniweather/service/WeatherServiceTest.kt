package com.miniweather.service

import com.miniweather.model.*
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import junit.framework.TestCase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WeatherServiceTest {

    @Mock
    private lateinit var mockNetworkService: NetworkService

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
        weatherService = WeatherService(mockNetworkService)
    }

    @Test
    fun whenGetWeather_andNetworkServiceSucceeds_returnsWeather() {
        val weatherCaptor = argumentCaptor<(response: WeatherResponse?, success: Boolean) -> Unit>()
        var weather: Weather? = null
        var success = false

        weatherService.getWeather(fakeLat, fakeLon) { w, s ->
            weather = w
            success = s
        }

        verify(mockNetworkService).makeWeatherRequest(eq(fakeLat), eq(fakeLon), weatherCaptor.capture())
        weatherCaptor.firstValue.invoke(fakeWeatherResponse, true)

        assertTrue(success)
        assertTrue(fakeWeatherResponse.weatherList.first().icon.contains("01m"))
        assertEquals(fakeWeatherResponse.weatherList.first().condition, weather?.condition)
        assertEquals(fakeWeatherResponse.temp.value.toInt(), weather?.temperature)
        assertEquals(fakeWeatherResponse.wind.speed.toInt(), weather?.windSpeed)
        assertEquals("East", weather?.windDirection)
        assertEquals(fakeWeatherResponse.location, weather?.location)
    }

    @Test
    fun whenGetWeather_andNetworkServiceFails_returnsFailure() {
        val weatherCaptor = argumentCaptor<(response: WeatherResponse?, success: Boolean) -> Unit>()
        var success = true
        var weather: Weather? = null

        weatherService.getWeather(fakeLat, fakeLon) { w, s ->
            weather = w
            success = s
        }

        verify(mockNetworkService).makeWeatherRequest(eq(fakeLat), eq(fakeLon), weatherCaptor.capture())
        weatherCaptor.firstValue.invoke(null, false)

        TestCase.assertNull(weather)
        assertFalse(success)
    }

}

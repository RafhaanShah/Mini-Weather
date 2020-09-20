package com.miniweather.service

import com.miniweather.model.Condition
import com.miniweather.model.Temperature
import com.miniweather.model.WeatherResponse
import com.miniweather.model.Wind
import com.nhaarman.mockitokotlin2.*
import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("UNCHECKED_CAST")
@RunWith(RobolectricTestRunner::class)
class NetworkServiceTest {

    @Mock
    private lateinit var mockApiService: WeatherApiInterface

    private lateinit var networkService: NetworkService

    private val fakeWeatherResponse = WeatherResponse(
        weatherList = listOf(Condition("Clear", "01m")),
        temp = Temperature(42.0),
        wind = Wind(23.0, 70.0),
        location = "London, UK"
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        networkService = NetworkService(mockApiService)
    }

    @Test
    fun whenMakeWeatherRequest_andSuccessfulResponse_returnsTheResponse() {
        val callbackCaptor = argumentCaptor<Callback<WeatherResponse?>>()
        val mockResponse: Response<WeatherResponse?> = mock(Response::class.java) as Response<WeatherResponse?>
        val mockCall: Call<WeatherResponse> = mock(Call::class.java) as Call<WeatherResponse>

        var weatherResponse: WeatherResponse? = null
        var success = false

        whenever(mockApiService.getWeather(any(), any(), any(), any())).thenReturn(mockCall)
        whenever(mockResponse.body()).thenReturn(fakeWeatherResponse)

        networkService.makeWeatherRequest(1.0, 1.0) { resp, succ ->
            weatherResponse = resp
            success = succ
        }

        verify(mockApiService).getWeather(eq(1.0), eq(1.0), any(), any())
        verify(mockCall).enqueue(callbackCaptor.capture())

        val callback = callbackCaptor.firstValue
        callback.onResponse(null, mockResponse)

        assertEquals(fakeWeatherResponse, weatherResponse)
        assertTrue(success)
    }

    @Test
    fun whenMakeWeatherRequest_andFailureResponse_returnsFailure() {
        val callbackCaptor = argumentCaptor<Callback<WeatherResponse?>>()
        val mockResponse: Response<WeatherResponse?> = mock(Response::class.java) as Response<WeatherResponse?>
        val mockCall: Call<WeatherResponse> = mock(Call::class.java) as Call<WeatherResponse>

        var weatherResponse: WeatherResponse? = null
        var success = true

        whenever(mockApiService.getWeather(any(), any(), any(), any())).thenReturn(mockCall)
        whenever(mockResponse.body()).thenReturn(fakeWeatherResponse)

        networkService.makeWeatherRequest(1.0, 1.0) { resp, succ ->
            weatherResponse = resp
            success = succ
        }

        verify(mockApiService).getWeather(eq(1.0), eq(1.0), any(), any())
        verify(mockCall).enqueue(callbackCaptor.capture())

        val callback = callbackCaptor.firstValue
        callback.onFailure(null, null)

        assertNull(weatherResponse)
        assertFalse(success)
    }

}

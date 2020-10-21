package com.miniweather.service.network

import com.miniweather.model.*
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class NetworkServiceTest {

    @Mock
    private lateinit var mockApiService: WeatherApi

    private lateinit var networkService: NetworkService

    private val testDispatcher = TestCoroutineDispatcher()

    private val fakeWeatherResponse = WeatherResponse(
        weatherList = listOf(Condition("Clear", "01m")),
        temp = Temperature(42.0),
        wind = Wind(23.0, 70.0),
        location = "London, UK"
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        networkService = NetworkService(mockApiService, testDispatcher)
    }

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun whenMakeWeatherRequest_andSuccessfulResponse_returnsTheResponse() = runBlockingTest {
        whenever(mockApiService.getWeather(any(), any())).thenReturn(fakeWeatherResponse)

        val result = networkService.getWeather(1.0, 1.0)

        verify(mockApiService).getWeather(eq(1.0), eq(1.0))
        assertEquals(fakeWeatherResponse, (result as DataResult.Success).data)
    }

    @Test
    fun whenMakeWeatherRequest_andFailureResponse_returnsFailure() = runBlockingTest {
        whenever(mockApiService.getWeather(any(), any())).thenThrow(RuntimeException("Something went wrong"))

        val result = networkService.getWeather(1.0, 1.0)

        verify(mockApiService).getWeather(eq(1.0), eq(1.0))
        assertTrue(result is DataResult.Failure)
    }

}

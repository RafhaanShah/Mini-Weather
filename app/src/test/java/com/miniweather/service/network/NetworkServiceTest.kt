package com.miniweather.service.network

import com.miniweather.testutil.BaseTest
import com.miniweather.testutil.fakeError
import com.miniweather.testutil.fakeLocation
import com.miniweather.testutil.fakeWeatherResponse
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

@ExperimentalCoroutinesApi
class NetworkServiceTest : BaseTest() {

    @Mock
    private lateinit var mockWeatherApi: WeatherApi

    private lateinit var networkService: NetworkService

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        networkService = NetworkService(mockWeatherApi, testDispatcher)
    }

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun whenMakeWeatherRequest_andSuccessfulResponse_returnsTheResponse() = runBlockingTest {
        whenever(mockWeatherApi.getWeather(any(), any())).thenReturn(fakeWeatherResponse)

        val actual = networkService.getWeather(fakeLocation)

        verify(mockWeatherApi).getWeather(
            fakeLocation.latitude,
            fakeLocation.longitude
        )
        assertEquals(fakeWeatherResponse, (actual.getOrThrow()))
    }

    @Test
    fun whenMakeWeatherRequest_andFailureResponse_returnsFailure() = runBlockingTest {
        whenever(mockWeatherApi.getWeather(any(), any())).thenThrow(
            RuntimeException(
                fakeError
            )
        )

        val actual = networkService.getWeather(fakeLocation)

        verify(mockWeatherApi).getWeather(
            fakeLocation.latitude,
            fakeLocation.longitude
        )
        assertTrue(actual.isFailure)
    }

}

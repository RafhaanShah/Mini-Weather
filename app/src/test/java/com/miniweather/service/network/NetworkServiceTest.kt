package com.miniweather.service.network

import com.miniweather.model.DataResult
import com.miniweather.testutil.FakeDataProvider
import com.nhaarman.mockitokotlin2.any
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

    private val fakeLocation = FakeDataProvider.provideFakeLocation()
    private val fakeWeatherResponse = FakeDataProvider.provideFakeWeatherResponse()

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

        val actual = networkService.getWeather(fakeLocation)

        verify(mockApiService).getWeather(fakeLocation.latitude, fakeLocation.longitude)
        assertEquals(fakeWeatherResponse, (actual as DataResult.Success).data)
    }

    @Test
    fun whenMakeWeatherRequest_andFailureResponse_returnsFailure() = runBlockingTest {
        whenever(mockApiService.getWeather(any(), any())).thenThrow(RuntimeException("Something went wrong"))

        val actual = networkService.getWeather(fakeLocation)

        verify(mockApiService).getWeather(fakeLocation.latitude, fakeLocation.longitude)
        assertTrue(actual is DataResult.Failure)
    }

}

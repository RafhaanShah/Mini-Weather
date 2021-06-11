package com.miniweather.service.network

import com.miniweather.repository.api.WeatherApi
import com.miniweather.testutil.BaseTest
import com.miniweather.testutil.fakeError
import com.miniweather.testutil.fakeLocation
import com.miniweather.testutil.fakeWeatherResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class NetworkServiceTest : BaseTest() {

    @MockK
    private lateinit var mockWeatherApi: WeatherApi

    private lateinit var networkService: NetworkService

    @Before
    fun setup() {
        networkService = NetworkService(mockWeatherApi)
    }

    @Test
    fun whenCallSucceeds_returnsResult() = runBlockingTest {
        coEvery { mockWeatherApi.getWeather(any(), any()) } returns fakeWeatherResponse

        val actual =
            networkService.call { getWeather(fakeLocation.latitude, fakeLocation.longitude) }

        coVerify {
            mockWeatherApi.getWeather(
                fakeLocation.latitude,
                fakeLocation.longitude
            )
        }

        assertEquals(fakeWeatherResponse, (actual.getOrThrow()))
    }

    @Test
    fun whenCallFails_returnsFailure() = runBlockingTest {
        coEvery { mockWeatherApi.getWeather(any(), any()) } throws RuntimeException(fakeError)

        val actual =
            networkService.call { getWeather(fakeLocation.latitude, fakeLocation.longitude) }

        coVerify {
            mockWeatherApi.getWeather(
                fakeLocation.latitude,
                fakeLocation.longitude
            )
        }

        assertTrue(actual.isFailure)
    }

}

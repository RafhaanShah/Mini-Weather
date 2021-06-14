package com.miniweather.service.network

import com.google.common.truth.Truth.assertThat
import com.miniweather.model.WeatherResponse
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

        val actual = call()

        assertThat(actual.getOrThrow()).isEqualTo(fakeWeatherResponse)
    }

    @Test
    fun whenCallFails_returnsFailure() = runBlockingTest {
        coEvery { mockWeatherApi.getWeather(any(), any()) } throws RuntimeException(fakeError)

        val actual = call()

        assertThat(actual.isFailure).isTrue()
    }

    private suspend fun call(): Result<WeatherResponse> {
        val actual =
            networkService.call { getWeather(fakeLocation.latitude, fakeLocation.longitude) }

        coVerify {
            mockWeatherApi.getWeather(
                fakeLocation.latitude,
                fakeLocation.longitude
            )
        }

        return actual
    }

}

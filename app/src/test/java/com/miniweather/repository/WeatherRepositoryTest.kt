package com.miniweather.repository

import com.miniweather.mapper.WeatherResponseMapper
import com.miniweather.model.Location
import com.miniweather.provider.DateTimeProvider
import com.miniweather.repository.api.WeatherApi
import com.miniweather.repository.dao.WeatherDao
import com.miniweather.service.database.DatabaseService
import com.miniweather.service.network.NetworkService
import com.miniweather.testutil.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherRepositoryTest : BaseTest() {

    @get:Rule
    val coroutinesTestRule = CoroutineTestRule()

    private lateinit var mockDatabaseService: DatabaseService

    private lateinit var mockNetworkService: NetworkService

    @MockK
    private lateinit var mockDateTimeProvider: DateTimeProvider

    @MockK
    private lateinit var weatherResponseMapper: WeatherResponseMapper

    @MockK
    private lateinit var mockWeatherDao: WeatherDao

    @MockK
    private lateinit var mockWeatherApi: WeatherApi

    private lateinit var weatherRepository: WeatherRepository

    private val fakeLocationWithDecimals =
        Location(
            latitude = 10.0011,
            longitude = 10.0099
        )

    private val fakeLocationRounded =
        Location(
            latitude = 10.00,
            longitude = 10.01
        )

    @Before
    fun setup() {
        every { mockDateTimeProvider.getCurrentTime() } returns fakeTimestamp

        mockNetworkService = NetworkService(mockWeatherApi)
        mockDatabaseService = DatabaseService(mockWeatherDao)

        weatherRepository = WeatherRepository(
            mockDatabaseService,
            mockDateTimeProvider,
            mockNetworkService,
            weatherResponseMapper
        )
    }

    @Test
    fun whenGetWeather_andNetworkServiceSucceeds_returnsWeather_andCaches() = runBlockingTest {
        coEvery {
            mockWeatherApi.getWeather(
                fakeLocationWithDecimals.latitude,
                fakeLocationWithDecimals.longitude
            )
        } returns fakeWeatherResponse
        every { weatherResponseMapper.map(fakeWeatherResponse, fakeLocationRounded) } returns
                fakeWeather

        val actual = weatherRepository.getWeather(fakeLocationWithDecimals)

        coVerify { mockWeatherDao.deleteInvalidCaches(fakeTimestamp - maxCacheAge) }
        coVerify { mockWeatherDao.insertIntoCache(fakeWeather) }

        assertEquals(fakeWeather, actual.getOrThrow())
    }

    @Test
    fun whenGetWeather_andNetworkServiceFails_andValidCacheExists_returnsWeather() =
        runBlockingTest {
            coEvery {
                mockWeatherApi.getWeather(
                    fakeLocationWithDecimals.latitude,
                    fakeLocationWithDecimals.longitude
                )
            } throws RuntimeException(fakeError)
            coEvery {
                mockWeatherDao.getCachedData(
                    fakeLocationRounded.latitude,
                    fakeLocationRounded.longitude,
                    fakeTimestamp - maxCacheAge
                )
            } returns fakeWeather

            val actual = weatherRepository.getWeather(fakeLocationWithDecimals)

            assertEquals(fakeWeather, actual.getOrThrow())
        }

    @Test
    fun whenGetWeather_andNetworkServiceFails_andNoValidCache_returnsFailure() = runBlockingTest {
        coEvery {
            mockWeatherApi.getWeather(
                fakeLocationWithDecimals.latitude,
                fakeLocationWithDecimals.longitude
            )
        } throws RuntimeException(fakeError)
        coEvery {
            mockWeatherDao.getCachedData(
                fakeLocationRounded.latitude,
                fakeLocationRounded.longitude,
                fakeTimestamp - maxCacheAge
            )
        } throws RuntimeException(fakeError)

        val actual = weatherRepository.getWeather(fakeLocationWithDecimals)

        assertTrue(actual.isFailure)
    }

}

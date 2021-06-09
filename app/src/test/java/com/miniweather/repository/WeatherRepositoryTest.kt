package com.miniweather.repository

import com.miniweather.mapper.WeatherResponseMapper
import com.miniweather.model.Location
import com.miniweather.provider.DateTimeProvider
import com.miniweather.repository.api.WeatherApi
import com.miniweather.repository.dao.WeatherDao
import com.miniweather.service.database.DatabaseService
import com.miniweather.service.network.NetworkService
import com.miniweather.testutil.*
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock

@ExperimentalCoroutinesApi
class WeatherRepositoryTest : BaseTest() {

    @get:Rule
    val coroutinesTestRule = CoroutineTestRule()

    private lateinit var mockDatabaseService: DatabaseService

    private lateinit var mockNetworkService: NetworkService

    @Mock
    private lateinit var mockDateTimeProvider: DateTimeProvider

    @Mock
    private lateinit var weatherResponseMapper: WeatherResponseMapper

    @Mock
    private lateinit var mockWeatherDao: WeatherDao

    @Mock
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
        whenever(mockDateTimeProvider.getCurrentTime()).thenReturn(fakeTimestamp)

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
        whenever(
            mockWeatherApi.getWeather(
                fakeLocationWithDecimals.latitude,
                fakeLocationWithDecimals.longitude
            )
        ).thenReturn(fakeWeatherResponse)
        whenever(weatherResponseMapper.map(fakeWeatherResponse, fakeLocationRounded)).thenReturn(
            fakeWeather
        )

        val actual = weatherRepository.getWeather(fakeLocationWithDecimals)

        verify(mockWeatherDao).deleteInvalidCaches(fakeTimestamp - maxCacheAge)
        verify(mockWeatherDao).insertIntoCache(fakeWeather)

        assertEquals(fakeWeather, actual.getOrThrow())
    }

    @Test
    fun whenGetWeather_andNetworkServiceFails_andValidCacheExists_returnsWeather() =
        runBlockingTest {
            whenever(
                mockWeatherApi.getWeather(
                    fakeLocationWithDecimals.latitude,
                    fakeLocationWithDecimals.longitude
                )
            ).thenThrow(RuntimeException(fakeError))
            whenever(
                mockWeatherDao.getCachedData(
                    fakeLocationRounded.latitude,
                    fakeLocationRounded.longitude,
                    fakeTimestamp - maxCacheAge
                )
            ).thenReturn(
                fakeWeather
            )

            val actual = weatherRepository.getWeather(fakeLocationWithDecimals)

            assertEquals(fakeWeather, actual.getOrThrow())
        }

    @Test
    fun whenGetWeather_andNetworkServiceFails_andNoValidCache_returnsFailure() = runBlockingTest {
        whenever(
            mockWeatherApi.getWeather(
                fakeLocationWithDecimals.latitude,
                fakeLocationWithDecimals.longitude
            )
        ).thenThrow(RuntimeException(fakeError))
        whenever(
            mockWeatherDao.getCachedData(
                fakeLocationRounded.latitude,
                fakeLocationRounded.longitude,
                fakeTimestamp - maxCacheAge
            )
        ).thenThrow(RuntimeException(fakeError))

        val actual = weatherRepository.getWeather(fakeLocationWithDecimals)

        assertTrue(actual.isFailure)
    }

}

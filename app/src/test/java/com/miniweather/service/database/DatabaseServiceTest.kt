package com.miniweather.service.database

import com.miniweather.repository.dao.WeatherDao
import com.miniweather.testutil.BaseTest
import com.miniweather.testutil.fakeLocation
import com.miniweather.testutil.fakeTimestamp
import com.miniweather.testutil.fakeWeather
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

@ExperimentalCoroutinesApi
class DatabaseServiceTest : BaseTest() {

    @Mock
    private lateinit var mockWeatherDao: WeatherDao

    private lateinit var databaseService: DatabaseService

    @Before
    fun setup() {
        databaseService = DatabaseService(mockWeatherDao)
    }

    @Test
    fun whenExecuteSucceeds_returnsResult() = runBlockingTest {
        whenever(mockWeatherDao.getCachedData(any(), any(), any())).thenReturn(fakeWeather)

        val actual = databaseService.execute {
            getCachedData(
                fakeLocation.latitude,
                fakeLocation.longitude,
                fakeTimestamp
            )
        }

        verify(mockWeatherDao).getCachedData(
            fakeLocation.latitude,
            fakeLocation.longitude,
            fakeTimestamp
        )

        assertEquals(fakeWeather, actual.getOrThrow())
    }

    @Test
    fun whenExecuteFails_returnsFailure() = runBlockingTest {
        whenever(mockWeatherDao.getCachedData(any(), any(), any())).thenReturn(null)

        val actual = databaseService.execute {
            getCachedData(
                fakeLocation.latitude,
                fakeLocation.longitude,
                fakeTimestamp
            )
        }

        verify(mockWeatherDao).getCachedData(
            fakeLocation.latitude,
            fakeLocation.longitude,
            fakeTimestamp
        )

        Assert.assertTrue(actual.isFailure)
    }

}

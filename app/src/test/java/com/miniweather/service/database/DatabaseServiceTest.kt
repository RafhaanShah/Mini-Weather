package com.miniweather.service.database

import com.miniweather.repository.dao.WeatherDao
import com.miniweather.testutil.BaseTest
import com.miniweather.testutil.fakeLocation
import com.miniweather.testutil.fakeTimestamp
import com.miniweather.testutil.fakeWeather
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DatabaseServiceTest : BaseTest() {

    @MockK
    private lateinit var mockWeatherDao: WeatherDao

    private lateinit var databaseService: DatabaseService

    @Before
    fun setup() {
        databaseService = DatabaseService(mockWeatherDao)
    }

    @Test
    fun whenExecuteSucceeds_returnsResult() = runBlockingTest {
        coEvery { mockWeatherDao.getCachedData(any(), any(), any()) } returns fakeWeather

        val actual = databaseService.execute {
            getCachedData(
                fakeLocation.latitude,
                fakeLocation.longitude,
                fakeTimestamp
            )
        }

        coVerify {
            mockWeatherDao.getCachedData(
                fakeLocation.latitude,
                fakeLocation.longitude,
                fakeTimestamp
            )
        }

        assertEquals(fakeWeather, actual.getOrThrow())
    }

    @Test
    fun whenExecuteFails_returnsFailure() = runBlockingTest {
        coEvery { mockWeatherDao.getCachedData(any(), any(), any()) } throws Throwable()

        val actual = databaseService.execute {
            getCachedData(
                fakeLocation.latitude,
                fakeLocation.longitude,
                fakeTimestamp
            )
        }

        coVerify {
            mockWeatherDao.getCachedData(
                fakeLocation.latitude,
                fakeLocation.longitude,
                fakeTimestamp
            )
        }

        Assert.assertTrue(actual.isFailure)
    }

}

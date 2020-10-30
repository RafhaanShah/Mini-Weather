package com.miniweather.service.database

import com.miniweather.testutil.BaseTest
import com.miniweather.testutil.fakeLocation
import com.miniweather.testutil.fakeTimestamp
import com.miniweather.testutil.fakeWeather
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

@ExperimentalCoroutinesApi
class DatabaseServiceTest : BaseTest() {

    @Mock
    private lateinit var mockWeatherDao: WeatherDao

    private lateinit var databaseService: DatabaseService

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        databaseService = DatabaseService(mockWeatherDao, testDispatcher)
    }

    @After
    fun tearDown() {
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun whenGetCachedData_callsDao() = runBlockingTest {
        whenever(mockWeatherDao.getCachedData(any(), any(), any())).thenReturn(listOf(fakeWeather))

        val actual = databaseService.getCachedData(fakeLocation, fakeTimestamp)

        verify(mockWeatherDao).getCachedData(fakeLocation.latitude, fakeLocation.longitude, fakeTimestamp)
        assertEquals(listOf(fakeWeather), actual)
    }

    @Test
    fun whenInsertIntoCache_callsDao() = runBlockingTest {
        databaseService.insertIntoCache(fakeWeather)

        verify(mockWeatherDao).insertIntoCache(fakeWeather)
    }

    @Test
    fun whenDeleteInvalidCaches_callsDao() = runBlockingTest {
        databaseService.deleteInvalidCaches(fakeTimestamp)

        verify(mockWeatherDao).deleteInvalidCaches(fakeTimestamp)
    }

}

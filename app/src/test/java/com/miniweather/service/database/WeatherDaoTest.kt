package com.miniweather.service.database

import androidx.room.Room
import com.miniweather.testutil.BaseInstrumentedTest
import com.miniweather.testutil.fakeTimestamp
import com.miniweather.testutil.fakeWeather
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class WeatherDaoTest : BaseInstrumentedTest() {

    private lateinit var weatherDao: WeatherDao

    private lateinit var database: WeatherDatabase

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(application, WeatherDatabase::class.java)
            .setTransactionExecutor(testDispatcher.asExecutor())
            .setQueryExecutor(testDispatcher.asExecutor())
            .allowMainThreadQueries()
            .build()

        weatherDao = database.weatherDao()
    }

    @After
    fun tearDown() {
        database.close()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun whenValidCacheDataIsInserted_itIsReturnedInTheGet() = runBlockingTest {
        weatherDao.insertIntoCache(fakeWeather)
        weatherDao.insertIntoCache(fakeWeather)
        weatherDao.insertIntoCache(fakeWeather.copy(latitude = 10.0, longitude = 10.0))

        val actual = weatherDao.getCachedData(
            fakeWeather.latitude,
            fakeWeather.longitude,
            fakeWeather.timestamp - TimeUnit.MINUTES.toMillis(10)
        )

        assertEquals(1, actual.size)
        assertEquals(fakeWeather, actual.first())
    }

    @Test
    fun whenNoValidCacheDataExists_emptyListReturnedInTheGet() = runBlockingTest {
        weatherDao.insertIntoCache(fakeWeather)

        val actual = weatherDao.getCachedData(
            fakeWeather.latitude,
            fakeWeather.longitude,
            fakeWeather.timestamp + TimeUnit.MINUTES.toMillis(10)
        )

        assertEquals(0, actual.size)
    }

    @Test
    fun whenDeleteInvalidCaches_itDeletesTheCorrectRows() = runBlockingTest {
        val oldTimeStamp = fakeTimestamp - TimeUnit.MINUTES.toMillis(10)
        val maxAge = fakeTimestamp - TimeUnit.MINUTES.toMillis(5)

        val oldWeather = fakeWeather.copy(
            latitude = 10.0, longitude = 10.0,
            timestamp = oldTimeStamp
        )

        weatherDao.insertIntoCache(fakeWeather)
        weatherDao.insertIntoCache(oldWeather)
        weatherDao.deleteInvalidCaches(maxAge)

        var actual = weatherDao.getCachedData(
            fakeWeather.latitude,
            fakeWeather.longitude,
            maxAge
        )

        assertEquals(1, actual.size)

        actual = weatherDao.getCachedData(
            oldWeather.latitude,
            oldWeather.longitude,
            maxAge
        )

        assertEquals(0, actual.size)
    }

}

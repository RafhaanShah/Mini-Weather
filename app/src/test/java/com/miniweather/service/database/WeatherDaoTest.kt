package com.miniweather.service.database

import androidx.room.Room
import com.miniweather.testutil.BaseInstrumentedTest
import com.miniweather.testutil.fakeWeather
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherDaoTest : BaseInstrumentedTest() {

    private lateinit var weatherDao: WeatherDao

    private lateinit var database: AppDatabase

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
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
        weatherDao.insertIntoCache(fakeWeather.copy(latitude = 3.3, longitude = 3.3))

        val actual = weatherDao.getCachedData(
            fakeWeather.latitude,
            fakeWeather.longitude,
            fakeWeather.timestamp - 1000
        )

        assertEquals(1, actual.size)
        assertEquals(fakeWeather, actual.first())
    }

    @Test
    fun whenNoValidCacheDataExists_emptyListReturnedInTheGet() = runBlockingTest {
        weatherDao.insertIntoCache(fakeWeather)

        var actual = weatherDao.getCachedData(
            fakeWeather.latitude,
            fakeWeather.longitude,
            fakeWeather.timestamp + 1000
        )

        assertEquals(0, actual.size)

        actual = weatherDao.getCachedData(
            fakeWeather.longitude,
            fakeWeather.latitude,
            fakeWeather.timestamp - 1000
        )

        assertEquals(0, actual.size)
    }

    @Test
    fun whenDeleteInvalidCaches_itDeletesTheCorrectRows() = runBlockingTest {
        val fakeWeather2 = fakeWeather.copy(latitude = 3.3, longitude = 3.3, timestamp = 100)
        weatherDao.insertIntoCache(fakeWeather)
        weatherDao.insertIntoCache(fakeWeather2)

        weatherDao.deleteInvalidCaches(900)

        var actual = weatherDao.getCachedData(
            fakeWeather.latitude,
            fakeWeather.longitude,
            500
        )

        assertEquals(1, actual.size)

        actual = weatherDao.getCachedData(
            fakeWeather2.latitude,
            fakeWeather2.longitude,
            500
        )

        assertEquals(0, actual.size)
    }

}

package com.miniweather.repository.dao

import androidx.room.Room
import com.google.common.truth.Truth.assertThat
import com.miniweather.database.WeatherDatabase
import com.miniweather.testutil.BaseInstrumentedTest
import com.miniweather.testutil.CoroutineTestRule
import com.miniweather.testutil.fakeTimestamp
import com.miniweather.testutil.fakeWeather
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class WeatherDaoTest : BaseInstrumentedTest() {

    @get:Rule
    val coroutinesTestRule = CoroutineTestRule()

    private lateinit var weatherDao: WeatherDao

    private lateinit var database: WeatherDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(application, WeatherDatabase::class.java)
            .setTransactionExecutor(coroutinesTestRule.testDispatcher.asExecutor())
            .setQueryExecutor(coroutinesTestRule.testDispatcher.asExecutor())
            .allowMainThreadQueries()
            .build()

        weatherDao = database.weatherDao()
    }

    @After
    fun tearDown() {
        database.close()
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

        assertThat(actual).isEqualTo(fakeWeather)
    }

    @Test
    fun whenNoValidCacheDataExists_emptyListReturnedInTheGet() = runBlockingTest {
        weatherDao.insertIntoCache(fakeWeather)

        val actual = weatherDao.getCachedData(
            fakeWeather.latitude,
            fakeWeather.longitude,
            fakeWeather.timestamp + TimeUnit.MINUTES.toMillis(10)
        )

        assertThat(actual).isNull()
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

        assertThat(actual).isEqualTo(fakeWeather)

        actual = weatherDao.getCachedData(
            oldWeather.latitude,
            oldWeather.longitude,
            maxAge
        )

        assertThat(actual).isNull()
    }

}

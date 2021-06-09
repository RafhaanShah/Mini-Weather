package com.miniweather.service.database

import com.miniweather.repository.dao.WeatherDao
import javax.inject.Inject

// IO dispatcher is not needed for Room
// https://www.lukaslechner.com/do-i-need-to-call-suspend-functions-of-retrofit-and-room-on-a-background-thread/
class DatabaseService @Inject constructor(
    private val weatherDao: WeatherDao
) {

    suspend fun <T> execute(query: suspend WeatherDao.() -> T): Result<T> =
        runCatching { checkNotNull(query(weatherDao)) }

}

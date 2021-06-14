package com.miniweather.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.miniweather.model.Weather
import com.miniweather.repository.dao.WeatherDao

internal const val databaseName: String = "weather_database"

@Database(entities = [Weather::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

}

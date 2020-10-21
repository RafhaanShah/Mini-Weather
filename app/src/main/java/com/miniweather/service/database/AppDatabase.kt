package com.miniweather.service.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.miniweather.model.Weather

@Database(entities = [Weather::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

}

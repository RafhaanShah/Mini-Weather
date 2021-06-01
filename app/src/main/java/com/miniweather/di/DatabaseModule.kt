package com.miniweather.di

import android.content.Context
import androidx.room.Room
import com.miniweather.service.database.WeatherDao
import com.miniweather.service.database.WeatherDatabase
import com.miniweather.service.database.databaseName
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface DatabaseModule {

    companion object {

        @Singleton
        @Provides
        fun provideAppDatabase(context: Context): WeatherDatabase = Room.databaseBuilder(
            context,
            WeatherDatabase::class.java, databaseName
        ).build()

        @Singleton
        @Provides
        fun provideWeatherDao(weatherDatabase: WeatherDatabase): WeatherDao = weatherDatabase.weatherDao()

    }

}

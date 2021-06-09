package com.miniweather.di

import android.content.Context
import androidx.room.Room
import com.miniweather.database.WeatherDatabase
import com.miniweather.database.databaseName
import com.miniweather.repository.dao.WeatherDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [DaoModule::class])
interface DatabaseModule {

    companion object {

        @Singleton
        @Provides
        fun provideAppDatabase(context: Context): WeatherDatabase = Room.databaseBuilder(
            context,
            WeatherDatabase::class.java, databaseName
        ).build()

    }

}

@Module
interface DaoModule {

    companion object {

        @Provides
        fun provideWeatherDao(weatherDatabase: WeatherDatabase): WeatherDao =
            weatherDatabase.weatherDao()

    }

}

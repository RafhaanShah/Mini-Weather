package com.miniweather.di

import android.content.Context
import androidx.room.Room
import com.miniweather.service.database.AppDatabase
import com.miniweather.service.database.WeatherDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
interface DatabaseModule {

    companion object {

        @Singleton
        @Provides
        fun provideAppDatabase(context: Context): AppDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database"
        ).build()

        @Singleton
        @Provides
        fun provideWeatherDao(appDatabase: AppDatabase): WeatherDao = appDatabase.weatherDao()

    }

}

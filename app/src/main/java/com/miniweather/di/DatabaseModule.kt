package com.miniweather.di

import android.content.Context
import androidx.room.Room
import com.miniweather.service.database.AppDatabase
import com.miniweather.service.database.DatabaseService
import com.miniweather.service.database.WeatherDao
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Named
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabaseService(weatherDao: WeatherDao, @Named("IO") dispatcher: CoroutineDispatcher):
            DatabaseService = DatabaseService(weatherDao, dispatcher)

    @Singleton
    @Provides
    fun provideWeatherDao(appDatabase: AppDatabase): WeatherDao = appDatabase.weatherDao()

    @Singleton
    @Provides
    fun provideAppDatabase(context: Context): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "database"
    ).build()

}

package com.miniweather.di

import android.content.Context
import androidx.room.Room
import com.miniweather.service.database.WeatherDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [DaoModule::class])
interface TestDatabaseModule {

    companion object {

        @Singleton
        @Provides
        fun provideAppDatabase(context: Context): WeatherDatabase =
            Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java)
                .build()

    }

}

package com.miniweather.di

import android.content.Context
import com.miniweather.app.WeatherApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: WeatherApplication) {

    @Provides
    @Singleton
    fun provideContext(): Context = app

}

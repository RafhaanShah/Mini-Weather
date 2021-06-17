package com.miniweather.di

import com.miniweather.ui.weather.WeatherContract
import com.miniweather.ui.weather.WeatherPresenter
import dagger.Binds
import dagger.Module

@Module
interface PresenterModule {

    @Binds
    fun provideWeatherPresenter(weatherPresenter: WeatherPresenter): WeatherContract.Presenter
}

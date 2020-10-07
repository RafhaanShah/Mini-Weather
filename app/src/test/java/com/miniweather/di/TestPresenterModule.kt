package com.miniweather.di

import com.miniweather.ui.WeatherContract
import dagger.Module
import dagger.Provides
import org.mockito.Mockito
import javax.inject.Singleton

@Module
class TestPresenterModule {

    @Singleton
    @Provides
    fun provideWeatherPresenter(): WeatherContract.Presenter = Mockito.mock(WeatherContract.Presenter::class.java)

}

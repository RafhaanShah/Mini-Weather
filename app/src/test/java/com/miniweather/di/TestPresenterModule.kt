package com.miniweather.di

import com.miniweather.ui.WeatherContract
import dagger.Module
import dagger.Provides
import org.mockito.Mockito

@Module
class TestPresenterModule {

    @Provides
    fun provideWeatherPresenter(): WeatherContract.Presenter = Mockito.mock(WeatherContract.Presenter::class.java)

}

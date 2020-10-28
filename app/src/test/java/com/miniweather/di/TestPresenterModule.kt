package com.miniweather.di

import com.miniweather.ui.weather.WeatherContract
import dagger.Module
import dagger.Provides
import org.mockito.Mockito

@Module
interface TestPresenterModule {

    companion object {

        @Provides
        fun provideWeatherPresenter(): WeatherContract.Presenter = Mockito.mock(WeatherContract.Presenter::class.java)

    }

}

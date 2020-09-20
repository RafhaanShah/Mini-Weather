package com.miniweather.di

import com.miniweather.service.LocationService
import com.miniweather.service.SharedPreferenceService
import com.miniweather.service.TimeService
import com.miniweather.service.WeatherService
import com.miniweather.ui.WeatherContract
import com.miniweather.ui.WeatherPresenter
import dagger.Module
import dagger.Provides

@Module
class PresenterModule {

    @Provides
    fun provideWeatherPresenter(
        locationService: LocationService, weatherService: WeatherService,
        sharedPreferenceService: SharedPreferenceService,
        timeService: TimeService
    ): WeatherContract.Presenter {
        return WeatherPresenter(locationService, weatherService, sharedPreferenceService, timeService)
    }
}

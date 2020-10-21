package com.miniweather.di

import com.miniweather.service.location.LocationService
import com.miniweather.service.util.TimeService
import com.miniweather.service.weather.WeatherService
import com.miniweather.ui.WeatherContract
import com.miniweather.ui.WeatherPresenter
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Named

@Module
class PresenterModule {

    @Provides
    fun provideWeatherPresenter(
        locationService: LocationService,
        timeService: TimeService,
        weatherService: WeatherService,
        @Named("Main") dispatcher: CoroutineDispatcher
    ): WeatherContract.Presenter = WeatherPresenter(locationService, timeService, weatherService, dispatcher)

}

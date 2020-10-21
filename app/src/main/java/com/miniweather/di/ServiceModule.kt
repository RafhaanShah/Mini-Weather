package com.miniweather.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.miniweather.service.database.DatabaseService
import com.miniweather.service.location.LocationService
import com.miniweather.service.network.ImageService
import com.miniweather.service.network.NetworkService
import com.miniweather.service.util.StringResourceService
import com.miniweather.service.util.TimeService
import com.miniweather.service.weather.WeatherService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ServiceModule {

    @Singleton
    @Provides
    fun provideLocationService(context: Context):
            LocationService = LocationService(LocationServices.getFusedLocationProviderClient(context))

    @Singleton
    @Provides
    fun provideWeatherService(
        networkService: NetworkService,
        databaseService: DatabaseService,
        timeService: TimeService,
        stringResourceService: StringResourceService
    ): WeatherService = WeatherService(networkService, timeService, databaseService, stringResourceService)

    @Singleton
    @Provides
    fun provideImageService(): ImageService = ImageService()

    @Singleton
    @Provides
    fun provideTimeService(): TimeService = TimeService()

    @Singleton
    @Provides
    fun provideStringResourceService(context: Context): StringResourceService = StringResourceService(context.resources)

}

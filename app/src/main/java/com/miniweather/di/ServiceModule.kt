package com.miniweather.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.miniweather.service.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ServiceModule {

    @Singleton
    @Provides
    fun provideLocationService(context: Context): LocationService {
        return LocationService(LocationServices.getFusedLocationProviderClient(context))
    }

    @Singleton
    @Provides
    fun provideWeatherService(
        networkService: NetworkService,
        stringResourceService: StringResourceService
    ): WeatherService {
        return WeatherService(networkService, stringResourceService)
    }

    @Singleton
    @Provides
    fun provideNetworkService(weatherApi: WeatherApi, stringResourceService: StringResourceService): NetworkService {
        return NetworkService(weatherApi, stringResourceService)
    }

    @Singleton
    @Provides
    fun provideImageService(): ImageService {
        return ImageService()
    }

    @Singleton
    @Provides
    fun provideSharedPreferenceService(context: Context): SharedPreferenceService {
        return SharedPreferenceService(context.getSharedPreferences("cache", Context.MODE_PRIVATE))
    }

    @Singleton
    @Provides
    fun provideTimeService(): TimeService {
        return TimeService()
    }

    @Singleton
    @Provides
    fun provideStringResourceService(context: Context): StringResourceService {
        return StringResourceService(context.resources)
    }
}

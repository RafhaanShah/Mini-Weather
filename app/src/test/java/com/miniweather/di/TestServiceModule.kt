package com.miniweather.di

import com.miniweather.service.*
import dagger.Module
import dagger.Provides
import org.mockito.Mockito

@Module
class TestServiceModule {

    @Provides
    fun provideLocationService(): LocationService = Mockito.mock(LocationService::class.java)

    @Provides
    fun provideWeatherService(): WeatherService = Mockito.mock(WeatherService::class.java)

    @Provides
    fun provideNetworkService(): NetworkService = Mockito.mock(NetworkService::class.java)

    @Provides
    fun provideImageService(): ImageService = Mockito.mock(ImageService::class.java)

    @Provides
    fun provideSharedPreferenceService(): SharedPreferenceService = Mockito.mock(SharedPreferenceService::class.java)

    @Provides
    fun provideTimeService(): TimeService = Mockito.mock(TimeService::class.java)
}

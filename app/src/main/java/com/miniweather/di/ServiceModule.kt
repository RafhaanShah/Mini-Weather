package com.miniweather.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.miniweather.BuildConfig
import com.miniweather.service.*
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class ServiceModule {

    @Provides
    fun provideLocationService(context: Context): LocationService {
        return LocationService(LocationServices.getFusedLocationProviderClient(context))
    }

    @Provides
    fun provideWeatherService(networkService: NetworkService): WeatherService {
        return WeatherService(networkService)
    }

    @Provides
    fun provideNetworkService(): NetworkService {
        val httpClient = OkHttpClient.Builder()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()

        val service = retrofit.create(WeatherApiInterface::class.java)

        return NetworkService(service)
    }

    @Provides
    fun provideImageService(): ImageService {
        return ImageService()
    }

    @Provides
    fun provideSharedPreferenceService(context: Context): SharedPreferenceService {
        return SharedPreferenceService(context.getSharedPreferences("cache", Context.MODE_PRIVATE))
    }

    @Provides
    fun provideTimeService(): TimeService {
        return TimeService()
    }
}

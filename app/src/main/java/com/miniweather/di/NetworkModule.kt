package com.miniweather.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.miniweather.provider.BaseUrlProvider
import com.miniweather.repository.api.WeatherApi
import dagger.Module
import dagger.Provides
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
interface NetworkModule {

    companion object {

        @ExperimentalSerializationApi
        @Singleton
        @Provides
        fun provideWeatherRetrofit(baseUrlProvider: BaseUrlProvider, json: Json): Retrofit =
            Retrofit.Builder()
                .baseUrl(baseUrlProvider.getBaseWeatherUrl())
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .client(OkHttpClient.Builder().build())
                .build()

        @Singleton
        @Provides
        fun provideWeatherApi(retrofit: Retrofit): WeatherApi =
            retrofit.create(WeatherApi::class.java)

        @Provides
        fun provideJsonSerializer(): Json {
            return Json {
                ignoreUnknownKeys = true
            }
        }

    }

}

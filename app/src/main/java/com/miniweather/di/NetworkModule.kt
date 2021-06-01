package com.miniweather.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.miniweather.app.BaseApplication
import com.miniweather.service.network.WeatherApi
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
        fun provideRetrofit(): Retrofit = Retrofit.Builder()
            .baseUrl("http://localhost/")
            .addConverterFactory(Json {
                ignoreUnknownKeys = true
            }.asConverterFactory("application/json".toMediaType()))
            .client(OkHttpClient.Builder().build())
            .build()

        @Singleton
        @Provides
        fun provideWeatherApi(retrofit: Retrofit): WeatherApi =
            retrofit.create(WeatherApi::class.java)

        @Provides
        fun provideBaseUrlProvider(applicationContext: Context) =
            (applicationContext as BaseApplication).getBaseUrlProvider()
    }

}

package com.miniweather.repository.api

import com.miniweather.BuildConfig
import com.miniweather.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET(weatherPath)
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): WeatherResponse

}

internal const val weatherPath = "data/2.5/weather?units=metric&appid=" + BuildConfig.API_KEY

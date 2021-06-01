package com.miniweather.service.network

import com.miniweather.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface WeatherApi {

    @GET
    suspend fun getWeather(
        @Url url: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): WeatherResponse

}



package com.miniweather.repository.api

import com.miniweather.BuildConfig
import com.miniweather.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal const val pathWeather = "data/2.5/weather"
internal const val paramLat = "lat"
internal const val paramLon = "lon"

interface WeatherApi {

    @GET(pathWeather)
    suspend fun getWeather(
        @Query(paramLat) lat: Double,
        @Query(paramLon) lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") appId: String = BuildConfig.WEATHER_API_KEY,
    ): WeatherResponse

}

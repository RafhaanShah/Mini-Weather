package com.miniweather.service

import com.miniweather.BuildConfig
import com.miniweather.model.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather?units=metric&appid=" + BuildConfig.API_KEY)
    fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Call<WeatherResponse>
}

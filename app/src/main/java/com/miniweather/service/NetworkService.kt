package com.miniweather.service

import com.miniweather.BuildConfig
import com.miniweather.model.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class NetworkService @Inject constructor(private val apiService: WeatherApiInterface) {

    fun makeWeatherRequest(
        lat: Double,
        lon: Double,
        callback: (response: WeatherResponse?, success: Boolean) -> Unit
    ) {
        val call = apiService.getWeather(lat, lon, BuildConfig.API_KEY, "metric")
        call.enqueue(object : Callback<WeatherResponse?> {

            override fun onResponse(
                call: Call<WeatherResponse?>?,
                response: Response<WeatherResponse?>
            ) {
                callback.invoke(response.body(), true)
            }

            override fun onFailure(call: Call<WeatherResponse?>?, t: Throwable?) {
                callback.invoke(null, false)
            }
        })
    }

}

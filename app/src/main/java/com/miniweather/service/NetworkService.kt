package com.miniweather.service

import com.miniweather.R
import com.miniweather.model.DataResult
import com.miniweather.model.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class NetworkService @Inject constructor(
    private val apiService: WeatherApi,
    private val stringResourceService: StringResourceService
) {

    fun makeWeatherRequest(
        lat: Double,
        lon: Double,
        callback: (DataResult<WeatherResponse>) -> Unit
    ) {
        val call = apiService.getWeather(lat, lon)
        call.enqueue(object : Callback<WeatherResponse?> {

            override fun onResponse(
                call: Call<WeatherResponse?>?,
                response: Response<WeatherResponse?>
            ) {
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    callback.invoke(DataResult.Success(body))
                } else {
                    callback.invoke(
                        DataResult.Failure(Exception(stringResourceService.getString(R.string.error_network)))
                    )
                }
            }

            override fun onFailure(call: Call<WeatherResponse?>?, t: Throwable?) {
                callback.invoke(
                    DataResult.Failure(
                        Exception(t?.message ?: stringResourceService.getString(R.string.error_network))
                    )
                )
            }
        })
    }

}

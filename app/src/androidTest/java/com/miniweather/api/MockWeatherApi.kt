package com.miniweather.api

import com.miniweather.repository.api.paramLat
import com.miniweather.repository.api.paramLon
import com.miniweather.repository.api.pathWeather
import com.miniweather.testutil.MockRequest
import com.miniweather.testutil.TestHttpCall
import com.miniweather.testutil.fakeLocation
import okhttp3.mockwebserver.MockResponse

object MockWeatherApi {

    val GET_WEATHER_SUCCESS: TestHttpCall = TestHttpCall(
        mockRequest = MockRequest(
            path = pathWeather,
            queryParams = mapOf(
                paramLat to fakeLocation.latitude.toString(),
                paramLon to fakeLocation.longitude.toString()
            )
        ),
        responseFile = "weather.json",
    )

    val GET_WEATHER_FAILURE: TestHttpCall = TestHttpCall(
        mockRequest = MockRequest(pathWeather),
        mockResponse = MockResponse().setResponseCode(500)
    )
}

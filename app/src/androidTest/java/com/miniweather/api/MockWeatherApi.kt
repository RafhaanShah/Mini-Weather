package com.miniweather.api

import com.miniweather.repository.api.pathWeather
import com.miniweather.testutil.MockRequest
import com.miniweather.testutil.TestHttpCall
import okhttp3.mockwebserver.MockResponse

object MockWeatherApi {

    val GET_WEATHER_SUCCESS: TestHttpCall = TestHttpCall(
        mockRequest = MockRequest(pathWeather),
        responseFile = "weather.json",
    )

    val GET_WEATHER_FAILURE: TestHttpCall = TestHttpCall(
        mockRequest = MockRequest(pathWeather),
        mockResponse = MockResponse().setResponseCode(500)
    )
}

package com.miniweather.testutil

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import com.miniweather.app.IntegrationTestApplication
import com.miniweather.database.WeatherDatabase
import com.miniweather.di.TestAppComponent
import com.miniweather.model.Location
import com.miniweather.provider.BaseUrlProvider
import com.miniweather.provider.DateTimeProvider
import io.mockk.every
import io.mockk.mockk
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse

class TestMocksHandler(
    private val appContext: Context,
    private val instrumentationContext: Context,
    private val webserver: MockWebServerRule
) {

    @Inject
    lateinit var mockBaseUrlProvider: BaseUrlProvider

    @Inject
    lateinit var mockDateTimeProvider: DateTimeProvider

    @Inject
    lateinit var mockFusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var mockDb: WeatherDatabase

    private val injector: TestAppComponent
        get() = (appContext as IntegrationTestApplication).appComponent

    fun initialise() {
        injector.inject(this)
        initMockBaseUrl(webserver.getPort())
        setMockTime(fakeTimestamp)
        setMockLocation(fakeLocation)
    }

    fun setMockTime(timeInMillis: Long) {
        every { mockDateTimeProvider.getCurrentTime() } returns timeInMillis
    }

    @SuppressLint("MissingPermission")
    fun setMockLocation(location: Location) {
        val mockLocation: android.location.Location = mockk()
        val mockLocationTask: Task<android.location.Location> = mockk()

        every {
            mockFusedLocationProviderClient.getCurrentLocation(any(), any())
        } returns mockLocationTask

        every { mockLocationTask.isComplete } returns true
        every { mockLocationTask.isCanceled } returns false
        every { mockLocationTask.exception } returns null
        every { mockLocationTask.result } returns mockLocation

        every { mockLocation.latitude } returns location.latitude
        every { mockLocation.longitude } returns location.longitude
    }

    fun expectHttpRequest(vararg testHttpCalls: TestHttpCall) {
        testHttpCalls.forEach {
            if (it.responseFile != null)
                it.mockResponse.setBody(readTestResourceFile("responses/${it.responseFile}"))
            webserver.expectMatchingRequest(it.mockRequest, it.mockResponse)
        }
    }

    fun <R> executeDbOperation(func: suspend WeatherDatabase.() -> R): R {
        return runBlocking {
            func(mockDb)
        }
    }

    fun readTestResourceFile(fileName: String) =
        instrumentationContext.assets.open(fileName).bufferedReader().readText()

    private fun initMockBaseUrl(port: Int) {
        every { mockBaseUrlProvider.weatherApi } returns "http://localhost:$port/"
        every { mockBaseUrlProvider.weatherImage } returns imageAssets
    }
}

data class TestHttpCall(
    val mockRequest: MockRequest = MockRequest(),
    val mockResponse: MockResponse = MockResponse().setResponseCode(200),
    val responseFile: String? = null
)

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
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import kotlinx.coroutines.runBlocking

class TestMocksHandler(
    private val appContext: Context,
    private val instrumentationContext: Context
) {

    @Inject
    lateinit var mockBaseUrlProvider: BaseUrlProvider

    @Inject
    lateinit var mockDateTimeProvider: DateTimeProvider

    @Inject
    lateinit var mockFusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var mockDb: WeatherDatabase

    private val webserver = TestWebServer()

    fun initialise() {
        ((appContext as IntegrationTestApplication).appComponent as TestAppComponent)
            .inject(this)
        webserver.start()

        initMockBaseUrl()
        setMockTime(fakeTimestamp)
        setMockLocation(fakeLocation)
    }

    fun terminate() {
        webserver.stop()
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
                it.mockResponse.setBody(readTestAssetFile("responses/${it.responseFile}"))
            webserver.expectRequest(it.mockRequest, it.mockResponse)
        }
    }

    fun <R> executeDbOperation(func: suspend WeatherDatabase.() -> R): R {
        return runBlocking {
            func(mockDb)
        }
    }

    private fun readTestAssetFile(fileName: String): String =
        InputStreamReader(
            instrumentationContext.assets.open(fileName),
            StandardCharsets.UTF_8
        ).buffered().readText()

    private fun initMockBaseUrl() {
        every { mockBaseUrlProvider.weatherApi } returns "http://localhost:${webserver.getPort()}/"
        every { mockBaseUrlProvider.weatherImage } returns imageAssets
    }
}

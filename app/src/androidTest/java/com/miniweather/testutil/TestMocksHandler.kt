package com.miniweather.testutil

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import com.miniweather.database.WeatherDatabase
import com.miniweather.di.TestAppComponent
import com.miniweather.model.Location
import com.miniweather.provider.BaseUrlProvider
import com.miniweather.provider.DateTimeProvider
import io.mockk.every
import io.mockk.mockk
import javax.inject.Inject

class TestMocksHandler {

    @Inject
    lateinit var mockBaseUrlProvider: BaseUrlProvider

    @Inject
    lateinit var mockDateTimeProvider: DateTimeProvider

    @Inject
    lateinit var mockFusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var mockDb: WeatherDatabase

    fun initMocks(testAppComponent: TestAppComponent) {
        testAppComponent.inject(this)

        initMockBaseUrl()
        setMockTime(fakeTimestamp)
        setMockLocation(fakeLocation)
    }

    fun setMockTime(timeInMillis: Long) {
        every { mockDateTimeProvider.getCurrentTime() } returns timeInMillis
    }

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

    private fun initMockBaseUrl() {
        every { mockBaseUrlProvider.getBaseWeatherUrl() } returns "http://localhost:${WebServer.getPort()}/"
        every { mockBaseUrlProvider.getBaseImageUrl() } returns imageAssets
    }

}

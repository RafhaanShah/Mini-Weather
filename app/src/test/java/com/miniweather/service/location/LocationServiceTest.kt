package com.miniweather.service.location

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import com.miniweather.testutil.BaseInstrumentedTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

@ExperimentalCoroutinesApi
class LocationServiceTest : BaseInstrumentedTest() {

    @Mock
    private lateinit var mockFusedLocationProviderClient: FusedLocationProviderClient

    @Mock
    private lateinit var mockLocationTask: Task<Location>

    @Mock
    private lateinit var mockLocation: Location

    private lateinit var locationService: LocationService

    @Before
    fun setup() {
        locationService = LocationService(mockFusedLocationProviderClient)
        whenever(mockFusedLocationProviderClient.getCurrentLocation(any(), any()))
            .thenReturn(mockLocationTask)
    }

    @Test
    fun whenGetLocationSucceeds_returnsLocation() = runBlockingTest {
        whenever(mockLocationTask.isComplete).thenReturn(true)
        whenever(mockLocationTask.isCanceled).thenReturn(false)
        whenever(mockLocationTask.result).thenReturn(mockLocation)
        whenever(mockLocation.latitude).thenReturn(1.0)
        whenever(mockLocation.longitude).thenReturn(2.0)

        val actual = locationService.getLocation()
        assertEquals(actual.latitude, 1.0, 0.0)
        assertEquals(actual.longitude, 2.0, 0.0)
    }

    @Test(expected = CancellationException::class)
    fun whenGetLocationFails_throwsException() = runBlockingTest {
        whenever(mockLocationTask.isComplete).thenReturn(true)
        whenever(mockLocationTask.isCanceled).thenReturn(true)

        locationService.getLocation()
    }

    @Test(expected = TimeoutCancellationException::class)
    fun whenGetLocationTimesOut_throwsException() = runBlockingTest {
        whenever(mockLocationTask.isComplete).thenReturn(false)

        locationService.getLocation()
    }

}

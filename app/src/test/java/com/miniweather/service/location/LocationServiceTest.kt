package com.miniweather.service.location

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import com.miniweather.testutil.BaseInstrumentedTest
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class LocationServiceTest : BaseInstrumentedTest() {

    @MockK
    private lateinit var mockFusedLocationProviderClient: FusedLocationProviderClient

    @MockK
    private lateinit var mockLocationTask: Task<Location>

    @MockK
    private lateinit var mockLocation: Location

    private lateinit var locationService: LocationService

    @Before
    fun setup() {
        locationService = LocationService(mockFusedLocationProviderClient)
        every {
            mockFusedLocationProviderClient.getCurrentLocation(any(), any())
        } returns mockLocationTask
        every { mockLocationTask.exception } returns null
        every { mockLocationTask.hint(Location::class).result } returns mockLocation
        every { mockLocationTask.addOnCompleteListener(any()) } returns mockLocationTask
    }

    @Test
    fun whenGetLocationSucceeds_returnsLocation() = runBlockingTest {
        every { mockLocationTask.isComplete } returns true
        every { mockLocationTask.isCanceled } returns false
        every { mockLocation.latitude } returns 1.0
        every { mockLocation.longitude } returns 2.0

        val actual = locationService.getLocation()
        assertEquals(actual.latitude, 1.0, 0.0)
        assertEquals(actual.longitude, 2.0, 0.0)
    }

    @Test(expected = CancellationException::class)
    fun whenGetLocationFails_throwsException() = runBlockingTest {
        every { mockLocationTask.isComplete } returns true
        every { mockLocationTask.isCanceled } returns true

        locationService.getLocation()
    }

    @Test(expected = TimeoutCancellationException::class)
    fun whenGetLocationTimesOut_throwsException() = runBlockingTest {
        every { mockLocationTask.isComplete } returns false

        locationService.getLocation()
    }

}

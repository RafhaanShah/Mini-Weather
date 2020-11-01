package com.miniweather.service.location

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.miniweather.testutil.BaseInstrumentedTest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
class LocationServiceTest : BaseInstrumentedTest() {

    @Mock
    private lateinit var mockFusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationService: LocationService

    @Before
    fun setup() {
        locationService = LocationService(mockFusedLocationProviderClient)
    }

    @Test
    fun whenGetLocation_requestsLocationUpdates() = runBlockingTest {
        val requestCaptor = argumentCaptor<LocationRequest>()
        val job = launch {
            locationService.getLocation()
        }

        verify(mockFusedLocationProviderClient).requestLocationUpdates(requestCaptor.capture(), any(), any())
        val request = requestCaptor.firstValue

        assertEquals(LocationRequest.PRIORITY_HIGH_ACCURACY, request.priority)
        assertEquals(1, request.numUpdates)

        job.cancel()
    }

    @Test
    fun whenGetLocationCancelled_removesLocationUpdates() = runBlockingTest {
        val locationCallbackCaptor = argumentCaptor<LocationCallback>()
        val job = launch {
            locationService.getLocation()
        }

        verify(mockFusedLocationProviderClient).requestLocationUpdates(any(), locationCallbackCaptor.capture(), any())

        job.cancel()

        verify(mockFusedLocationProviderClient).removeLocationUpdates(locationCallbackCaptor.firstValue)
    }

    @Test
    fun whenLocationResultCallbackInvoked_returnsLocation() = runBlockingTest {
        val locationCallbackCaptor = argumentCaptor<LocationCallback>()
        val mockLocationResult = mock(LocationResult::class.java)
        val mockLocation = mock(Location::class.java)

        whenever(mockLocationResult.lastLocation).thenReturn(mockLocation)
        whenever(mockLocation.latitude).thenReturn(1.1)
        whenever(mockLocation.longitude).thenReturn(2.2)

        launch {
            val actual = locationService.getLocation()
            assertEquals(1.1, actual.latitude, 0.0)
            assertEquals(2.2, actual.longitude, 0.0)
        }

        verify(mockFusedLocationProviderClient).requestLocationUpdates(any(), locationCallbackCaptor.capture(), any())
        val callback = locationCallbackCaptor.firstValue
        callback.onLocationResult(mockLocationResult)

        verify(mockFusedLocationProviderClient).removeLocationUpdates(callback)
    }

}

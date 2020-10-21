package com.miniweather.service.location

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocationServiceTest {

    @Mock
    private lateinit var mockFusedLocationClient: FusedLocationProviderClient

    private lateinit var locationService: LocationService

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        locationService = LocationService(mockFusedLocationClient)
    }

    @Test
    fun whenGetLocation_requestsLocationUpdates() {
        val requestCaptor = argumentCaptor<LocationRequest>()

        locationService.getLocation { _, _ -> }

        verify(mockFusedLocationClient).requestLocationUpdates(requestCaptor.capture(), any(), any())

        val request = requestCaptor.firstValue
        assertEquals(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, request.priority)
        assertEquals(1, request.numUpdates)
    }

    @Test
    fun whenLocationResultCallbackInvoked_invokesGetLocationCallback() {
        val locationCallbackCaptor = argumentCaptor<LocationCallback>()
        val mockLocationResult = mock(LocationResult::class.java)
        val mockLocation = mock(Location::class.java)

        whenever(mockLocationResult.lastLocation).thenReturn(mockLocation)
        whenever(mockLocation.latitude).thenReturn(1.1)
        whenever(mockLocation.longitude).thenReturn(2.2)

        var lat = 0.0
        var lon = 0.0

        locationService.getLocation { la, lo ->
            lat = la
            lon = lo
        }

        verify(mockFusedLocationClient).requestLocationUpdates(any(), locationCallbackCaptor.capture(), any())

        val callback = locationCallbackCaptor.firstValue
        callback.onLocationResult(mockLocationResult)

        verify(mockFusedLocationClient).removeLocationUpdates(callback)

        assertEquals(1.1, lat)
        assertEquals(2.2, lon)
    }

}

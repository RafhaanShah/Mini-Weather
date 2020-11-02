package com.miniweather.service.location

import android.annotation.SuppressLint
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.miniweather.model.Location
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume

class LocationService @Inject constructor(private val fusedLocationProviderClient: FusedLocationProviderClient) {

    private lateinit var locationCallback: LocationCallback

    suspend fun getLocation(): Location = suspendCancellableCoroutine { continuation ->
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                    continuation.resume(
                        Location(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)
                    )
                }
            }
        }

        requestLocation(createLocationRequest())
        continuation.invokeOnCancellation { fusedLocationProviderClient.removeLocationUpdates(locationCallback) }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation(request: LocationRequest) {
        fusedLocationProviderClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setNumUpdates(1)
            .setInterval(TimeUnit.SECONDS.toMillis(10))
            .setFastestInterval(TimeUnit.SECONDS.toMillis(1))
            .setExpirationDuration(TimeUnit.SECONDS.toMillis(30))
    }

}

package com.miniweather.service.location

import android.annotation.SuppressLint
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import javax.inject.Inject

class LocationService @Inject constructor(private val fusedLocationClient: FusedLocationProviderClient) {

    private lateinit var locationCallback: LocationCallback

    @SuppressLint("MissingPermission")
    fun getLocation(callback: (lat: Double, lon: Double) -> Unit) {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                    callback.invoke(
                        locationResult.lastLocation.latitude,
                        locationResult.lastLocation.longitude
                    )
                }
            }
        }

        val request = LocationRequest.create()
        request.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        request.numUpdates = 1
        request.setExpirationDuration(60000)

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

}

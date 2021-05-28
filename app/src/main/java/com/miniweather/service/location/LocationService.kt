package com.miniweather.service.location

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource
import com.miniweather.model.Location
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LocationService @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) {

    @SuppressLint("MissingPermission")
    @Throws(TimeoutCancellationException::class)
    suspend fun getLocation(): Location = withTimeout(TimeUnit.SECONDS.toMillis(30)) {
        val location = fusedLocationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
            CancellationTokenSource().token
        ).await()

        Location(location.latitude, location.longitude)
    }

}

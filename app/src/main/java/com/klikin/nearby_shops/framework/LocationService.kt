package com.klikin.nearby_shops.framework

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

class LocationService {
    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    suspend fun getUserLocation(context: Context): Location? {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val isUserLocationPermissionGranted = true
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled =
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!isGPSEnabled || !isUserLocationPermissionGranted) {
            return null
        }

        return suspendCancellableCoroutine { cont ->
            fusedLocationProviderClient.lastLocation.apply {
                if (isComplete) {
                    if (isSuccessful) {
                        cont.resume(result) {}
                    } else {
                        cont.resume(null) {}
                    }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener {
                    cont.resume(it) {}
                }
                addOnFailureListener {
                    cont.resume(null) {}
                }
                addOnCanceledListener {
                    cont.resume(null) {}
                }
            }
        }
    }

    fun calculateDistanceInMeters(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
}

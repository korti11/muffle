package io.korti.muffle.location

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationManager @Inject constructor(
    val context: Context,
    val firebaseRemoteConfig: FirebaseRemoteConfig
) {

    companion object {
        val TAG = LocationManager::class.java.simpleName
    }

    private val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val locationPendingIntent: PendingIntent by lazy {
        val intent = Intent(context, LocationBroadcastReceiver::class.java)
        intent.action = LocationBroadcastReceiver.ACTION_PROCESS_UPDATES
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
    private val locationRequest: LocationRequest by lazy {
        val request = LocationRequest()
        val updateInterval = firebaseRemoteConfig.getLong("update_interval")
        val fastestInterval = firebaseRemoteConfig.getLong("fastest_update_interval")
        val maxWaitTime = firebaseRemoteConfig.getLong("max_wait_time")

        Log.d(TAG, "Update interval: $updateInterval")
        Log.d(TAG, "Fastest interval: $fastestInterval")
        Log.d(TAG, "Max wait time: $maxWaitTime")

        request.interval = updateInterval
        request.fastestInterval = fastestInterval
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        request.maxWaitTime = maxWaitTime
        request
    }

    fun requestLocationUpdates() {
        Log.i(TAG, "Request for location updates")
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationPendingIntent)
    }

    suspend fun getLastKnownLocation(): Location = withContext(Dispatchers.Default) {
        if (fusedLocationProviderClient.lastLocation != null) {
            val location = fusedLocationProviderClient.lastLocation.await()
            if (location != null) {
                return@withContext location
            }
        }
        Location("muffle_application")
    }
}
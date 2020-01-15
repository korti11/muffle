package io.korti.muffle.location

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationManager @Inject constructor(val context: Context) {

    companion object {
        private const val UPDATE_INVERVAL: Long  = 60000 * 5  // Every 5 minutes
        private const val FASTEST_UPDATE_INTERVAL: Long = (60000 * 2.5).toLong() // Every 2.5 minutes
        private const val MAX_WAIT_TIME: Long = 60000 * 10    // Every 10 minutes

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
        request.interval = UPDATE_INVERVAL
        request.fastestInterval = FASTEST_UPDATE_INTERVAL
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        request.maxWaitTime = MAX_WAIT_TIME
        request
    }

    fun requestLocationUpdates() {
        Log.i(TAG, "Request for location updates")
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationPendingIntent)
    }

    suspend fun getLastKnownLocation(): Location = withContext(Dispatchers.Default) {
        if(fusedLocationProviderClient.lastLocation != null) {
            val location = fusedLocationProviderClient.lastLocation.await()
            if (location != null) {
                return@withContext location
            }
        }
        Location("muffle_application")
    }
}
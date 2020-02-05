/*
 * Copyright 2020 Korti
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
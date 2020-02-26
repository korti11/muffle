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

package io.korti.muffle.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import io.korti.muffle.database.dao.MufflePointDao
import io.korti.muffle.database.entity.MufflePoint
import io.korti.muffle.location.LocationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.atan

class AddMufflePointActivityViewModel @Inject constructor(
    locationManager: LocationManager,
    private val mufflePointDao: MufflePointDao
) :
    ViewModel() {

    companion object {
        private val TAG = AddMufflePointActivityViewModel::class.java.simpleName
    }

    val mapCameraPosition = MutableLiveData(LatLng(0.0, 0.0))
    val mapCircle = MutableLiveData<Circle>()
    val mapMarker = MutableLiveData<Marker>()
    val mapZoom = MutableLiveData<Float>(getZoomLevel(150))

    init {
        viewModelScope.launch {
            val location = locationManager.getLastKnownLocation()
            mapCameraPosition.value = LatLng(location.latitude, location.longitude)
        }
    }

    fun updateCameraPosition(pos: LatLng) {
        mapCameraPosition.value = pos
    }

    fun updateRadius(radius: Int) {
        val newRadius = radius.toDouble() + 100
        val zoomLevel = getZoomLevel(radius)
        Log.d(TAG, "Current Radius: ${mapCircle.value?.radius}, Current Zoom: ${mapZoom.value}")
        Log.d(TAG, "Radius: $newRadius, Zoom: $zoomLevel")
        mapCircle.value?.radius = newRadius
        mapZoom.value = zoomLevel
    }

    fun saveMufflePoint(
        name: String,
        image: Bitmap,
        ringtoneVolume: Int,
        mediaVolume: Int,
        notificationVolume: Int,
        alarmVolume: Int
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var latitude = 0.0
                var longitude = 0.0
                var radius = 0.0

                withContext(Dispatchers.Main) {
                    latitude = mapMarker.value!!.position.latitude
                    longitude = mapMarker.value!!.position.longitude
                    radius = mapCircle.value!!.radius
                }

                val base64Image = MufflePoint.bitmapToBase64(image)
                mufflePointDao.insertAll(
                    MufflePoint(
                        name = name,
                        lat = latitude,
                        lng = longitude,
                        radius = radius,
                        image = base64Image,
                        ringtoneVolume = ringtoneVolume,
                        mediaVolume = mediaVolume,
                        notificationVolume = notificationVolume,
                        alarmVolume = alarmVolume
                    )
                )
            }
        }
    }

    private fun getZoomLevel(radius: Int): Float {
        return 15F - (2.201F * atan(radius / 400.0).toFloat())
    }

}
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
import io.korti.muffle.database.dao.MufflePointDao
import io.korti.muffle.database.entity.MufflePoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.atan

class EditMufflePointActivityViewModel @Inject constructor(private val mufflePointDao: MufflePointDao) :
    ViewModel() {

    companion object {
        private val TAG = EditMufflePointActivityViewModel::class.java.simpleName
    }

    val mufflePoint = MutableLiveData<MufflePoint>()
    val mapCircle = MutableLiveData<Circle>()
    val mapZoom = MutableLiveData<Float>()

    fun loadMufflePoint(mufflePointId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val mufflePoint = mufflePointDao.getById(mufflePointId)
                withContext(Dispatchers.Main) {
                    mapZoom.value = getZoomLevel(mufflePoint.radius.toInt())
                    this@EditMufflePointActivityViewModel.mufflePoint.value = mufflePoint
                }
            }
        }
    }

    fun saveMufflePoint(
        name: String,
        image: Bitmap,
        enabled: Boolean,
        ringtoneVolume: Int,
        mediaVolume: Int,
        notificationVolume: Int,
        alarmVolume: Int
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var radius = 0.0

                withContext(Dispatchers.Main) {
                    radius = mapCircle.value!!.radius
                }

                val base64Image = MufflePoint.bitmapToBase64(image)
                val mufflePoint = this@EditMufflePointActivityViewModel.mufflePoint.value

                mufflePoint?.apply {
                    if(this.radius != radius) {
                        this.image = base64Image
                        this.radius = radius
                    }

                    if (status >= MufflePoint.Status.ENABLE && enabled.not()) {
                        status = MufflePoint.Status.DISABLED
                    } else if (status == MufflePoint.Status.DISABLED && enabled) {
                        status = MufflePoint.Status.ENABLE
                    }

                    this.name = name
                    this.ringtoneVolume = ringtoneVolume
                    this.mediaVolume = mediaVolume
                    this.notificationVolume = notificationVolume
                    this.alarmVolume = alarmVolume
                }

                if (mufflePoint != null) {
                    mufflePointDao.update(mufflePoint)
                }
            }
        }
    }

    fun deleteMufflePoint() {
        viewModelScope.launch {
            val mufflePoint = mufflePoint.value
            if(mufflePoint != null) {
                withContext(Dispatchers.IO) {
                    mufflePointDao.delete(mufflePoint)
                }
            }
        }
    }

    fun updateRadius(radius: Int) {
        val newRadius = radius.toDouble() + 100
        val zoomLevel = getZoomLevel(radius)
        Log.d(TAG, "Current Radius: ${mapCircle.value?.radius}, Current Zoom: ${mapZoom.value}")
        Log.d(TAG, "Radius: $newRadius, Zoom: $zoomLevel")
        mapCircle.value?.radius = newRadius
        mapZoom.value = zoomLevel
    }

    private fun getZoomLevel(radius: Int): Float {
        return 15F - (2.201F * atan(radius / 400.0).toFloat())
    }

}
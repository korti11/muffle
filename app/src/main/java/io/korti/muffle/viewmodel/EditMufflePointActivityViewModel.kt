package io.korti.muffle.viewmodel

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
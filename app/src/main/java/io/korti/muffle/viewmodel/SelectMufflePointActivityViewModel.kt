package io.korti.muffle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import io.korti.muffle.location.LocationManager
import kotlinx.coroutines.launch
import javax.inject.Inject

class SelectMufflePointActivityViewModel @Inject constructor(
    private val locationManager: LocationManager
) : ViewModel() {

    val cameraPosition = MutableLiveData<LatLng>(LatLng(0.0, 0.0))

    fun init() {
        updateCurrentLocation()
    }

    fun updateCurrentLocation() {
        viewModelScope.launch {
            updateCameraPosition()
        }
    }

    private suspend fun updateCameraPosition() {
        locationManager.getLastKnownLocation().also {
            cameraPosition.value = LatLng(it.latitude, it.longitude)
        }
    }

}
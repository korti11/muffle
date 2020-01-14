package io.korti.muffle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import io.korti.muffle.location.LocationManager
import kotlinx.coroutines.launch
import javax.inject.Inject

class SelectMufflePointActivityViewModel @Inject constructor(
    private val locationManager: LocationManager
) : ViewModel() {

    val cameraPosition = MutableLiveData<LatLng>(LatLng(0.0, 0.0))
    val selectedPosition = MutableLiveData<Marker>()

    fun init() {
        updateCameraToCurrentLocation()
    }

    fun updateCameraToCurrentLocation() {
        viewModelScope.launch {
            updateCameraPosition()
        }
    }

    fun updateLocation(marker: Marker) {
        selectedPosition.value?.remove()
        selectedPosition.value = marker
    }

    private suspend fun updateCameraPosition() {
        locationManager.getLastKnownLocation().also {
            cameraPosition.value = LatLng(it.latitude, it.longitude)
        }
    }

}
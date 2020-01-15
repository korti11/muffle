package io.korti.muffle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import io.korti.muffle.location.LocationManager
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddMufflePointActivityViewModel @Inject constructor(locationManager: LocationManager) : ViewModel() {

    val mapCameraPosition = MutableLiveData(LatLng(0.0, 0.0))
    val mapCircle = MutableLiveData<Circle>()
    val mapMarker = MutableLiveData<Marker>()

    init {
        viewModelScope.launch {
            val location = locationManager.getLastKnownLocation()
            mapCameraPosition.value = LatLng(location.latitude, location.longitude)
        }
    }

    fun updateCameraPosition(pos: LatLng) {
        mapCameraPosition.value = pos
    }

}
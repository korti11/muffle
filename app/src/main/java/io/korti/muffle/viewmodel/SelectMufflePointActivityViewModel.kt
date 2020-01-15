package io.korti.muffle.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import io.korti.muffle.R
import io.korti.muffle.location.LocationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URLEncoder
import javax.inject.Inject

class SelectMufflePointActivityViewModel @Inject constructor(
    private val locationManager: LocationManager,
    private val requestQueue: RequestQueue,
    private val context: Context
) : ViewModel() {

    companion object {
        private val TAG = SelectMufflePointActivityViewModel::class.java.simpleName
    }

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

    fun requestLocation(search: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val searchQuery = URLEncoder.encode(search, "utf-8")
                val jsonRequest = JsonObjectRequest(
                    Request.Method.GET,
                    context.getString(R.string.location_search_url, searchQuery),
                    JSONObject(),
                    Response.Listener<JSONObject> {
                        val coordinates =
                            it.getJSONArray("features").getJSONObject(0).getJSONObject("geometry")
                                .getJSONArray("coordinates")
                        cameraPosition.postValue(
                            LatLng(
                                coordinates.getDouble(1),
                                coordinates.getDouble(0)
                            )
                        )
                    }, Response.ErrorListener {
                        Log.e(
                            TAG,
                            "Something bad happened on the location request. Error: ${it.message}"
                        )
                    })
                requestQueue.add(jsonRequest)
            }
        }
    }

    private suspend fun updateCameraPosition() {
        locationManager.getLastKnownLocation().also {
            cameraPosition.value = LatLng(it.latitude, it.longitude)
        }
    }

}
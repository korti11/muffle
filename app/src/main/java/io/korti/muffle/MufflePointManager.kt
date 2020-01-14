package io.korti.muffle

import android.location.Location
import android.util.Log
import io.korti.muffle.audio.AudioManager
import io.korti.muffle.database.dao.MufflePointDao
import io.korti.muffle.database.entity.MufflePoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MufflePointManager @Inject constructor(
    private val mufflePointDao: MufflePointDao,
    private val audioManager: AudioManager
) {

    companion object {
        private val TAG = MufflePointManager::class.java.simpleName

        private const val NO_ACTIVE_POINT = 0
        private const val STILL_ACTIVE = 1
        private const val NOT_ACTIVE_ANYMORE = 2
    }

    suspend fun enableDisableMufflePoint(mufflePoint: MufflePoint) = withContext(Dispatchers.IO) {
        if(mufflePoint.status >= MufflePoint.Status.ENABLE) {
            mufflePointDao.updateStatus(mufflePoint.uid, MufflePoint.Status.DISABLED)
            if(mufflePoint.status == MufflePoint.Status.ACTIVE) {
                val newActiveArea = mufflePointDao.getInAreaMufflePoint()
                audioManager.reverseOrUpdateMuffle(newActiveArea)
            }
        } else {
            mufflePointDao.updateStatus(mufflePoint.uid, MufflePoint.Status.ENABLE)
        }
    }

    suspend fun processLocations(location: Location) = withContext(Dispatchers.Default) {
        // First check is the current active muffle point still valid
        val currentStatus = checkCurrentActiveLocation(location)
        // Check in area muffle points if they are still in area
        checkInAreaLocations(location, currentStatus)
        // Check and update enabled but currently not in area muffle points
        checkAreaLocations(location)
    }

    private suspend fun checkCurrentActiveLocation(location: Location): Int =
        withContext(Dispatchers.IO) {
            val currentActive = mufflePointDao.getActiveMufflePoint()
            if (currentActive != null) {
                val dummyLocation = Location("muffle_application").apply {
                    longitude = currentActive.lng
                    latitude = currentActive.lat
                }
                if (location.distanceTo(dummyLocation) <= currentActive.radius) {
                    Log.i(TAG, "Muffle point with id=${currentActive.uid} is still active.")
                    return@withContext STILL_ACTIVE
                } else {
                    Log.i(TAG, "Muffle point with id=${currentActive.uid} is not active anymore.")
                    mufflePointDao.updateStatus(currentActive.uid, MufflePoint.Status.ENABLE)
                    return@withContext NOT_ACTIVE_ANYMORE
                }
            }
            NO_ACTIVE_POINT
        }

    private suspend fun checkInAreaLocations(location: Location, currentStatus: Int) =
        withContext(Dispatchers.IO) {
            val dummyLocation = Location("muffle_application")
            val inAreaPoints = mufflePointDao.getInAreaMufflePoints().groupBy {
                dummyLocation.longitude = it.lng
                dummyLocation.latitude = it.lat
                val distance = location.distanceTo(dummyLocation)
                Log.d(TAG, "Distance between $location and $dummyLocation is $distance")
                distance <= it.radius
            }
            if (currentStatus == NOT_ACTIVE_ANYMORE) {
                val newActiveMufflePoint = inAreaPoints[true]?.first()
                if (newActiveMufflePoint != null) {
                    mufflePointDao.updateStatus(newActiveMufflePoint.uid, MufflePoint.Status.ACTIVE)
                    Log.i(
                        TAG,
                        "Muffle point with id=${newActiveMufflePoint.uid} changed status from in area to active."
                    )
                }
                audioManager.reverseOrUpdateMuffle(newActiveMufflePoint)
                Log.i(TAG, "Reversed or updated sound settings.")
            }
            inAreaPoints[false]?.forEach {
                mufflePointDao.updateStatus(it.uid, MufflePoint.Status.ENABLE)
            }
        }

    private suspend fun checkAreaLocations(location: Location) = withContext(Dispatchers.IO) {
        val dummyLocation = Location("muffle_application")
        val enabledMufflePoints = mufflePointDao.getEnabledMufflePoints().filter {
            dummyLocation.longitude = it.lng
            dummyLocation.latitude = it.lat
            val distance = location.distanceTo(dummyLocation)
            Log.d(TAG, "Distance between $location and $dummyLocation is $distance")
            distance <= it.radius
        }
        var skipFirst = false
        if (enabledMufflePoints.isNotEmpty()) {
            if (mufflePointDao.getActiveMufflePoint() == null) {
                val newActiveMufflePoint = enabledMufflePoints.first()
                mufflePointDao.updateStatus(newActiveMufflePoint.uid, MufflePoint.Status.ACTIVE)
                audioManager.muffleSounds(newActiveMufflePoint)
                Log.i(TAG, "Muffle point with id=${newActiveMufflePoint.uid} is now active.")
                skipFirst = true
            }
            (if (skipFirst) enabledMufflePoints.drop(1) else enabledMufflePoints).forEach {
                mufflePointDao.updateStatus(it.uid, MufflePoint.Status.IN_AREA)
            }
        }
    }
}
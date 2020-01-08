package io.korti.muffle

import android.util.Log
import com.google.android.gms.location.Geofence
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
    }

    suspend fun activateMufflePoints(geofences: List<Geofence>) = withContext(Dispatchers.Default) {
        val activeMufflePoint = activateMufflePointsDatabase(geofences)
        if(activeMufflePoint != null) {
            val result = audioManager.muffleSounds(activeMufflePoint)
            when (result) {
                AudioManager.MUFFLING_ERROR_CURRENT_VOLUME -> Log.e(
                    TAG,
                    "Could not save current audio levels for $activeMufflePoint"
                )
                AudioManager.MUFFLING_ERROR_VOLUME_NOT_UPDATED -> Log.wtf(
                    TAG,
                    "This should not happened. Why did this happen for $activeMufflePoint"
                )
                else -> Log.i(TAG, "Muffled volumes for ${activeMufflePoint.name}")
            }
        }
    }

    suspend fun deactivateMufflePoints(geofences: List<Geofence>) =
        withContext(Dispatchers.Default) {
            val activeMufflePoint = deactivateMufflePointsDatabase(geofences)
            val result = audioManager.reverseOrUpdateMuffle(activeMufflePoint)
            when (result) {
                AudioManager.MUFFLING_ERROR_CURRENT_VOLUME -> Log.e(
                    TAG,
                    "Could not load previous saved audio levels."
                )
                AudioManager.MUFFLING_ERROR_VOLUME_NOT_UPDATED -> Log.wtf(
                    TAG,
                    "This should not have happened. Why did this happen for $activeMufflePoint on updating the sound levels."
                )
                else -> Log.i(TAG, "Muffled volumes for ${activeMufflePoint?.name}")
            }
        }

    private suspend fun activateMufflePointsDatabase(geofences: List<Geofence>): MufflePoint? =
        withContext(Dispatchers.IO) {
            val validPoints = geofences.map(Geofence::getRequestId)
            val currentActive = mufflePointDao.getActiveMufflePoint() != null

            if(currentActive.not()) {
                mufflePointDao.updateStatus(validPoints.first(), MufflePoint.Status.ACTIVE)
            }
            validPoints.forEach {
                mufflePointDao.updateStatus(it, MufflePoint.Status.IN_AREA)
            }

            if(currentActive.not()) {
                mufflePointDao.getActiveMufflePoint()
            } else {
                null
            }
        }

    private suspend fun deactivateMufflePointsDatabase(geofences: List<Geofence>): MufflePoint? =
        withContext(Dispatchers.IO) {
            geofences.map(Geofence::getRequestId).forEach {
                mufflePointDao.updateStatus(it, MufflePoint.Status.ENABLE)
            }
            mufflePointDao.getInAreaMufflePoint()
        }

}
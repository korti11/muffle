package io.korti.muffle.location

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import io.korti.muffle.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

class GeofenceTransitionsJobIntentService : JobIntentService() {

    companion object {
        private const val JOB_ID = 13
        private val TAG = GeofenceTransitionsJobIntentService::class.java.simpleName

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, GeofenceTransitionsJobIntentService::class.java, JOB_ID, intent)
        }
    }

    private val job = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Default + job)

    /**
     * Called serially for each work dispatched to and processed by the service.  This
     * method is called on a background thread, so you can do long blocking operations
     * here.  Upon returning, that work will be considered complete and either the next
     * pending work dispatched here or the overall service destroyed now that it has
     * nothing else to do.
     *
     *
     * Be aware that when running as a job, you are limited by the maximum job execution
     * time and any single or total sequential items of work that exceeds that limit will
     * cause the service to be stopped while in progress and later restarted with the
     * last unfinished work.  (There is currently no limit on execution duration when
     * running as a pre-O plain Service.)
     *
     * @param intent The intent describing the work to now be processed.
     */
    override fun onHandleWork(intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = getErrorMessage(geofencingEvent)
            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Check if the user entered or exited a muffle point
        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // TODO: Add new active points to the audio manager.
            // AudioManager.addActivePoints(triggeringGeofences);
            Log.i(TAG, "Entered following muffle points: $triggeringGeofences")

        } else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // TODO: Remove active points from the audio manager.
            // AudioManager.removeActivePoints(triggeringGeofences);
            Log.i(TAG, "Exited following muffle points: $triggeringGeofences")

        } else {
            // Log error for invalid geofence transition
            Log.e(TAG,
                "Transition ${getTransitionName(geofenceTransition)} is invalid.")
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun getErrorMessage(geofencingEvent: GeofencingEvent): String
            = when(geofencingEvent.errorCode) {
        GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> getString(R.string.geofence_not_available)
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> getString(R.string.geofence_too_many_geofences)
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> getString(R.string.geofence_too_many_pending_intents)
        else -> getString(R.string.geofence_unknown_error)
    }

    private fun getTransitionName(transition: Int): String = when(transition) {
        Geofence.GEOFENCE_TRANSITION_ENTER -> getString(R.string.geofence_transition_enter)
        Geofence.GEOFENCE_TRANSITION_DWELL -> getString(R.string.geofence_transition_dwell)
        Geofence.GEOFENCE_TRANSITION_EXIT -> getString(R.string.geofence_transition_exit)
        else -> getString(R.string.geofence_transition_unknown)
    }
}
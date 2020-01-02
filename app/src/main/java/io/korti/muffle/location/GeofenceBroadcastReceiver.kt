package io.korti.muffle.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence.*
import com.google.android.gms.location.GeofenceStatusCodes.*
import com.google.android.gms.location.GeofencingEvent
import io.korti.muffle.R

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private val TAG = GeofenceBroadcastReceiver::class.java.simpleName

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.  During this time you can use the other methods on
     * BroadcastReceiver to view/modify the current result values.  This method
     * is always called within the main thread of its process, unless you
     * explicitly asked for it to be scheduled on a different thread using
     * [android.content.Context.registerReceiver]. When it runs on the main
     * thread you should
     * never perform long-running operations in it (there is a timeout of
     * 10 seconds that the system allows before considering the receiver to
     * be blocked and a candidate to be killed). You cannot launch a popup dialog
     * in your implementation of onReceive().
     *
     *
     * **If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
     * then the object is no longer alive after returning from this
     * function.** This means you should not perform any operations that
     * return a result to you asynchronously. If you need to perform any follow up
     * background work, schedule a [android.app.job.JobService] with
     * [android.app.job.JobScheduler].
     *
     * If you wish to interact with a service that is already running and previously
     * bound using [bindService()][android.content.Context.bindService],
     * you can use [.peekService].
     *
     *
     * The Intent filters used in [android.content.Context.registerReceiver]
     * and in application manifests are *not* guaranteed to be exclusive. They
     * are hints to the operating system about how to find suitable recipients. It is
     * possible for senders to force delivery to specific recipients, bypassing filter
     * resolution.  For this reason, [onReceive()][.onReceive]
     * implementations should respond only to known actions, ignoring any unexpected
     * Intents that they may receive.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = getErrorMessage(context!!, geofencingEvent)
            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Check if the user entered or exited a muffle point
        if(geofenceTransition == GEOFENCE_TRANSITION_DWELL) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // TODO: Add new active points to the audio manager.
            // AudioManager.addActivePoints(triggeringGeofences);
            Log.i(TAG, "Entered following muffle points: $triggeringGeofences")

        } else if(geofenceTransition == GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // TODO: Remove active points from the audio manager.
            // AudioManager.removeActivePoints(triggeringGeofences);
            Log.i(TAG, "Exited following muffle points: $triggeringGeofences")

        } else {
            // Log error for invalid geofence transition
            Log.e(TAG,
                "Transition ${getTransitionName(context!!, geofenceTransition)} is invalid.")
        }
    }

    private fun getErrorMessage(context: Context, geofencingEvent: GeofencingEvent): String
            = when(geofencingEvent.errorCode) {
        GEOFENCE_NOT_AVAILABLE -> context.getString(R.string.geofence_not_available)
        GEOFENCE_TOO_MANY_GEOFENCES -> context.getString(R.string.geofence_too_many_geofences)
        GEOFENCE_TOO_MANY_PENDING_INTENTS -> context.getString(R.string.geofence_too_many_pending_intents)
        else -> context.getString(R.string.geofence_unknown_error)
    }

    private fun getTransitionName(context: Context, transition: Int): String = when(transition) {
        GEOFENCE_TRANSITION_ENTER -> context.getString(R.string.geofence_transition_enter)
        GEOFENCE_TRANSITION_DWELL -> context.getString(R.string.geofence_transition_dwell)
        GEOFENCE_TRANSITION_EXIT -> context.getString(R.string.geofence_transition_exit)
        else -> context.getString(R.string.geofence_transition_unknown)
    }

}
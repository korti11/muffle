package io.korti.muffle.location

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.JobIntentService
import com.google.firebase.analytics.FirebaseAnalytics
import io.korti.muffle.MuffleApplication
import javax.inject.Inject

class LocationBootJobIntentService : JobIntentService() {

    companion object {
        private const val JOB_ID = 11
        private val TAG = LocationBootJobIntentService::class.java.simpleName

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, LocationBootJobIntentService::class.java, JOB_ID, intent)
        }
    }

    @Inject
    lateinit var locationManager: LocationManager
    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate() {
        (applicationContext as MuffleApplication).appComponent.inject(this)
        super.onCreate()
    }

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
        Log.i(TAG, "Requested location updates on startup.")
        firebaseAnalytics.logEvent("startup_location_request", Bundle())
        locationManager.requestLocationUpdates()
    }
}
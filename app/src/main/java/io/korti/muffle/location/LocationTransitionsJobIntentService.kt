/*
 * Copyright 2020 Korti
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.korti.muffle.location

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.google.android.gms.location.LocationResult
import io.korti.muffle.MuffleApplication
import io.korti.muffle.MufflePointManager
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class LocationTransitionsJobIntentService : JobIntentService() {

    companion object {
        private const val JOB_ID = 13
        private val TAG = LocationTransitionsJobIntentService::class.java.simpleName

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, LocationTransitionsJobIntentService::class.java, JOB_ID, intent)
        }
    }

    @Inject
    lateinit var mufflePointManager: MufflePointManager

    override fun onCreate() {
        super.onCreate()
        (applicationContext as MuffleApplication).appComponent.inject(this)
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
        if(intent != null) {
            val action = intent.action
            if (action == LocationBroadcastReceiver.ACTION_PROCESS_UPDATES) {
                val result = LocationResult.extractResult(intent)
                if(result != null) {
                    Log.i(TAG, "Process location ${result.lastLocation}")
                    runBlocking {
                        mufflePointManager.processLocations(result.lastLocation)
                    }   // Could not find a better solution for the time.
                }
            }
        }
    }
}
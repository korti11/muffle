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

package io.korti.muffle

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Room
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.Component
import io.korti.muffle.database.AppDatabase
import io.korti.muffle.database.MIGRATION_1_2
import io.korti.muffle.location.LocationBootJobIntentService
import io.korti.muffle.location.LocationManager
import io.korti.muffle.location.LocationTransitionsJobIntentService
import io.korti.muffle.module.ContextModule
import io.korti.muffle.module.DatabaseModule
import io.korti.muffle.module.FirebaseModule
import io.korti.muffle.module.NetworkModule

@Component(modules = [DatabaseModule::class, ContextModule::class, NetworkModule::class, FirebaseModule::class])
interface ApplicationComponent {

    fun inject(activity: MainActivity)

    fun inject(activity: AddMufflePointActivity)

    fun inject(activity: SelectMufflePointActivity)

    fun inject(activity: EditMufflePointActivity)

    fun inject(mufflePointManager: MufflePointManager)

    fun inject(locationTransitionsJobIntentService: LocationTransitionsJobIntentService)

    fun inject(locationBootJobIntentService: LocationBootJobIntentService)

}

class MuffleApplication : Application() {

    companion object {
        private val TAG = MuffleApplication::class.java.simpleName
        private lateinit var database: AppDatabase
        private lateinit var appContext: Context
        private lateinit var firebaseAnalytics: FirebaseAnalytics

        fun getDatabase(): AppDatabase {
            return database
        }

        fun getAppContext(): Context {
            return appContext
        }

        fun getFirebaseAnalytics(): FirebaseAnalytics {
            return firebaseAnalytics
        }
    }

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     *
     *
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     *
     *
     * If you override this method, be sure to call `super.onCreate()`.
     *
     *
     * Be aware that direct boot may also affect callback order on
     * Android [android.os.Build.VERSION_CODES.N] and later devices.
     * Until the user unlocks the device, only direct boot aware components are
     * allowed to run. You should consider that all direct boot unaware
     * components, including such [android.content.ContentProvider], are
     * disabled until user unlock happens, especially when component callback
     * order matters.
     */
    override fun onCreate() {
        super.onCreate()
        database = Room
            .databaseBuilder(this, AppDatabase::class.java, "muffle-database")
            .addMigrations(MIGRATION_1_2)
            .build()
        appContext = applicationContext
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600).build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        firebaseRemoteConfig.fetchAndActivate().run {
            addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "Config params updated.")
                    LocationManager(
                        this@MuffleApplication,
                        firebaseRemoteConfig
                    ).requestLocationUpdates()
                } else {
                    Log.e(TAG, "Could not update config params.")
                }
            }
        }
    }

    val appComponent: ApplicationComponent = DaggerApplicationComponent.create()

}
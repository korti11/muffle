package io.korti.muffle

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Component
import io.korti.muffle.database.AppDatabase
import io.korti.muffle.location.LocationTransitionsJobIntentService
import io.korti.muffle.module.ContextModule
import io.korti.muffle.module.DatabaseModule

@Component(modules = [DatabaseModule::class, ContextModule::class])
interface ApplicationComponent {

    fun inject(activity: MainActivity)

    fun inject(activity: SelectMufflePointActivity)

    fun inject(mufflePointManager: MufflePointManager)

    fun inject(locationTransitionsJobIntentService: LocationTransitionsJobIntentService)

}

class MuffleApplication : Application() {

    companion object {
        private lateinit var database: AppDatabase
        private lateinit var appContext: Context

        fun getDatabase(): AppDatabase {
            return database
        }

        fun getAppContext(): Context {
            return appContext
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
            .build()
        appContext = applicationContext
    }

    val appComponent: ApplicationComponent = DaggerApplicationComponent.create()

}
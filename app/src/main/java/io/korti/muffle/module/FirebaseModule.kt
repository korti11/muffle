package io.korti.muffle.module

import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import io.korti.muffle.MuffleApplication

@Module
class FirebaseModule {

    @Provides
    fun providesFirebaseAnalytics(): FirebaseAnalytics {
        return MuffleApplication.getFirebaseAnalytics()
    }

}
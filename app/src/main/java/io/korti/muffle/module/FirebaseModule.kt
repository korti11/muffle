package io.korti.muffle.module

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.Module
import dagger.Provides
import io.korti.muffle.MuffleApplication

@Module
class FirebaseModule {

    @Provides
    fun providesFirebaseAnalytics(): FirebaseAnalytics {
        return MuffleApplication.getFirebaseAnalytics()
    }

    @Provides
    fun providesFirebaseRemoteConfig(): FirebaseRemoteConfig {
        return FirebaseRemoteConfig.getInstance()
    }

}
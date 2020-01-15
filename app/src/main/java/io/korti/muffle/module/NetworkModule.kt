package io.korti.muffle.module

import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import dagger.Module
import dagger.Provides
import io.korti.muffle.MuffleApplication

@Module
class NetworkModule {

    @Provides
    fun providerRequestQueue(): RequestQueue {
        return Volley.newRequestQueue(MuffleApplication.getAppContext())
    }

}
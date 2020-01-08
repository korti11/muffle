package io.korti.muffle.module

import android.content.Context
import dagger.Module
import dagger.Provides
import io.korti.muffle.MuffleApplication

@Module
class ContextModule {

    @Provides
    fun provideContext(): Context {
        return MuffleApplication.getAppContext()
    }

}
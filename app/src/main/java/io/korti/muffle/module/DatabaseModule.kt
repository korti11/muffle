package io.korti.muffle.module

import dagger.Module
import dagger.Provides
import io.korti.muffle.MuffleApplication
import io.korti.muffle.database.dao.MufflePointDao

@Module
class DatabaseModule {

    @Provides
    fun provideMufflePointDao(): MufflePointDao {
        return MuffleApplication.getDatabase().getMufflePointDao()
    }

}
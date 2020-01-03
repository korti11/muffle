package io.korti.muffle.database

import androidx.room.Database
import androidx.room.RoomDatabase
import io.korti.muffle.database.dao.MufflePointDao
import io.korti.muffle.database.entity.MufflePoint

@Database(entities = [MufflePoint::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getMufflePointDao(): MufflePointDao

}
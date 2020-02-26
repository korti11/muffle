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

package io.korti.muffle.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.korti.muffle.database.dao.MufflePointDao
import io.korti.muffle.database.entity.MufflePoint

@Database(entities = [MufflePoint::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getMufflePointDao(): MufflePointDao

}

val MIGRATION_1_2 = object : Migration(1, 2) {
    /**
     * Should run the necessary migrations.
     *
     *
     * This class cannot access any generated Dao in this method.
     *
     *
     * This method is already called inside a transaction and that transaction might actually be a
     * composite transaction of all necessary `Migration`s.
     *
     * @param database The database instance
     */
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE new_muffle_point (
                uid INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                latitude REAL NOT NULL,
                longitude REAL NOT NULL,
                point_radius REAL NOT NULL,
                point_name TEXT NOT NULL,
                point_status INTEGER NOT NULL,
                maps_image TEXT NOT NULL,
                ringtone_volume INTEGER NOT NULL,
                media_volume INTEGER NOT NULL,
                notification_volume INTEGER NOT NULL,
                alarm_volume INTEGER NOT NULL
            )
        """.trimIndent())
        database.execSQL("""
            INSERT INTO new_muffle_point (latitude, longitude, point_radius, point_name,
             point_status, maps_image, ringtone_volume, media_volume, notification_volume,
             alarm_volume)
            SELECT latitude, longitude, point_radius, point_name,
             point_status, maps_image, ringtone_volume, media_volume, notification_volume,
             alarm_volume FROM muffle_point
        """.trimIndent())
        database.execSQL("DROP TABLE muffle_point")
        database.execSQL("ALTER TABLE new_muffle_point RENAME TO muffle_point")
    }

}
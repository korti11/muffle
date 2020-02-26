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

import android.database.Cursor
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        var db = helper.createDatabase(TEST_DB, 1).apply {
            execSQL("""
                INSERT INTO muffle_point (uid, latitude, longitude, point_radius, point_name,
                 point_status, maps_image, ringtone_volume, media_volume, notification_volume,
                 alarm_volume)
                VALUES ('test', 0.0, 0.0, 100.0, 'Test', 1, '', 1, 1, 1, 1)
            """.trimIndent())

            close()
        }

        db = helper.runMigrationsAndValidate(TEST_DB, 2, false, MIGRATION_1_2)

        db.apply {
            val cur = query("SELECT * FROM muffle_point")
            validate1To2Data(
                cur, mapOf(
                    Pair("uid", 1L),
                    Pair("latitude", 0.0),
                    Pair("longitude", 0.0),
                    Pair("point_radius", 100.0),
                    Pair("point_name", "Test"),
                    Pair("point_status", 1),
                    Pair("ringtone_volume", 1),
                    Pair("media_volume", 1),
                    Pair("notification_volume", 1),
                    Pair("alarm_volume", 1)
                )
            )
            cur.close()
            close()
        }
    }

    private fun validate1To2Data(cur: Cursor, validData: Map<String, Any>) {
        cur.moveToFirst()
        cur.columnNames.forEach {
            val index = cur.getColumnIndex(it)
            when(it) {
                "uid" -> assertEquals("The UID is not equal! Data migration not successful!",
                    validData["uid"] as Long, cur.getLong(index))
                "latitude" -> assertEquals("The latitude is not equal! Data migration not successful!",
                    validData["latitude"] as Double, cur.getDouble(index), 0.1)
                "longitude" -> assertEquals("The longitude is not equal! Data migration not successful!",
                    validData["longitude"] as Double, cur.getDouble(index), 0.1)
                "point_radius" -> assertEquals("The point_radius is not equal! Data migration not successful!",
                    validData["point_radius"] as Double, cur.getDouble(index), 0.1)
                "point_name" -> assertEquals("The point_radius is not equal! Data migration not successful!",
                    validData["point_name"] as String, cur.getString(index))
                "point_status" -> assertEquals("The point_status is not equal! Data migration not successful!",
                    validData["point_status"] as Int, cur.getInt(index))
                "ringtone_volume" -> assertEquals("The ringtone_volume is not equal! Data migration not successful!",
                    validData["ringtone_volume"] as Int, cur.getInt(index))
                "media_volume" -> assertEquals("The media_volume is not equal! Data migration not successful!",
                    validData["media_volume"] as Int, cur.getInt(index))
                "notification_volume" -> assertEquals("The notification_volume is not equal! Data migration not successful!",
                    validData["notification_volume"] as Int, cur.getInt(index))
                "alarm_volume" -> assertEquals("The point_status is not equal! Data migration not successful!",
                    validData["point_status"] as Int, cur.getInt(index))
            }
        }
    }

}
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

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.korti.muffle.database.dao.MufflePointDao
import io.korti.muffle.database.entity.MufflePoint
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseInstrumentTest {

    private lateinit var mufflePointDao: MufflePointDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        mufflePointDao = db.getMufflePointDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadMufflePointById() {
        val mufflePoint = MufflePoint(
            uid = "test-1",
            name = "Test 1",
            image = ""
        )
        mufflePointDao.insertAll(mufflePoint)
        val byId = mufflePointDao.getById("test-1")
        assertThat("Found the wrong muffle point by the id.", byId, equalTo(mufflePoint))
    }

    @Test
    @Throws(Exception::class)
    fun writeReadAndDeleteMufflePoint() {
        val mufflePoint = MufflePoint(
            uid = "test-1",
            name = "Test 1",
            image = ""
        )
        mufflePointDao.insertAll(mufflePoint)
        val byId = mufflePointDao.getById("test-1")
        mufflePointDao.delete(mufflePoint)
        val deletedById = mufflePointDao.getById("test-1")
        assertThat("Found the wrong muffle point by the id.", byId, equalTo(mufflePoint))
        assertNull("Muffle point is not deleted.", deletedById)
    }
}
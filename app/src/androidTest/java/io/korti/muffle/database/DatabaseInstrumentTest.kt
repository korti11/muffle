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
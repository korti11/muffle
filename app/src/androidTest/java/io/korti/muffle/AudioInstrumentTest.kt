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

package io.korti.muffle

import android.content.Context
import android.media.AudioManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import io.korti.muffle.database.dao.MufflePointDao
import io.korti.muffle.database.entity.MufflePoint
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AudioInstrumentTest {

    private lateinit var audioManager: AudioManager
    private lateinit var muffleAudioManager: io.korti.muffle.audio.AudioManager
    private lateinit var mufflePointDao: MufflePointDao

    @Before
    fun initTests() {
        audioManager =
            MuffleApplication.getAppContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        muffleAudioManager = io.korti.muffle.audio.AudioManager(MuffleApplication.getAppContext())
        mufflePointDao = MuffleApplication.getDatabase().getMufflePointDao()
        mufflePointDao.insertAll(
            MufflePoint(
                uid = 1,
                name = "Home",
                image = "",
                status = MufflePoint.Status.ACTIVE,
                ringtoneVolume = 5
            ),
            MufflePoint(
                uid = 2,
                name = "Work",
                status = MufflePoint.Status.DISABLED,
                image = "",
                ringtoneVolume = 4,
                mediaVolume = 3
            )
        )
    }

    @After
    fun deleteTestData() {
        mufflePointDao.delete(mufflePointDao.getById(1))
        mufflePointDao.delete(mufflePointDao.getById(2))
    }

    @Test
    fun enterMufflePoint() {
        runBlocking {
            val mufflePoint = mufflePointDao.getById(1)
            muffleAudioManager.muffleSounds(mufflePoint)
            assertThat(
                "The ringtone volume should be 5",
                audioManager.getStreamVolume(AudioManager.STREAM_RING),
                equalTo(5)
            )
        }
    }

    @Test
    fun enterAndExitMufflePoint() {
        runBlocking {
            val mufflePoint = mufflePointDao.getById(2)
            audioManager.setStreamVolume(AudioManager.STREAM_RING, 1, 0)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 0)
            muffleAudioManager.muffleSounds(mufflePoint)
            muffleAudioManager.reverseOrUpdateMuffle(null)
            assertThat(
                "The ringtone volume should be 1",
                audioManager.getStreamVolume(AudioManager.STREAM_RING),
                equalTo(1)
            )
            assertThat(
                "The media volume should be 1",
                audioManager.getStreamVolume(AudioManager.STREAM_MUSIC),
                equalTo(1)
            )
        }
    }

}
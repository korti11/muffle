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
                "home",
                name = "Home",
                image = "",
                status = MufflePoint.Status.ACTIVE,
                ringtoneVolume = 5
            ),
            MufflePoint(
                "work",
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
        mufflePointDao.delete(mufflePointDao.getById("home"))
        mufflePointDao.delete(mufflePointDao.getById("work"))
    }

    @Test
    fun enterMufflePoint() {
        runBlocking {
            val mufflePoint = mufflePointDao.getById("home")
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
            val mufflePoint = mufflePointDao.getById("work")
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
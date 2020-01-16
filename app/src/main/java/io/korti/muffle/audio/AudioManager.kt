package io.korti.muffle.audio

import android.content.Context
import android.media.AudioManager
import io.korti.muffle.R
import io.korti.muffle.database.entity.MufflePoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioManager @Inject constructor(context: Context) {

    companion object {
        const val MUFFLING_SUCCESS = 1
        const val MUFFLING_ERROR_CURRENT_VOLUME = 2
        const val MUFFLING_ERROR_VOLUME_NOT_UPDATED = 3
    }

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val audioLevelStorage = context.getSharedPreferences(
        context.getString(R.string.audio_levels_preferences_key),
        Context.MODE_PRIVATE
    )

    fun getMaxVolumeOfPhone(audioStream: Int): Int {
        return audioManager.getStreamMaxVolume(audioStream)
    }

    /**
     * Stores the current audio settings and replace it with the given from the muffle point.
     * @param mufflePoint Muffle point with the new audio settings.
     * @return Integer who represents the successful muffling or an error code.
     */
    suspend fun muffleSounds(mufflePoint: MufflePoint): Int = withContext(Dispatchers.Default) {

        // Save current audio levels
        if (audioLevelStorage == null) {
            MUFFLING_ERROR_CURRENT_VOLUME
        }

        with(audioLevelStorage.edit()) {
            putInt(
                "STREAM_RING",
                audioManager.getStreamVolume(AudioManager.STREAM_RING)
            )
            putInt(
                "STREAM_MUSIC",
                audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            )
            putInt(
                "STREAM_NOTIFICATION",
                audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
            )
            putInt(
                "STREAM_ALARM",
                audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
            )
            commit()
        }

        mufflePoint.apply {
            return@withContext setStreamVolumes(
                ringtoneVolume,
                mediaVolume,
                notificationVolume,
                alarmVolume
            )
        }
        0   // Idk why but it's needed 🤷‍♂️
    }

    /**
     * If the given muffle point is not null then it updates the audio settings accordingly or if
     * it is null it reverses to the previous stored audio settings.
     * @param mufflePoint Muffle point with the new audio settings.
     * @return Integer who represents the succesful muffling or an error code.
     */
    suspend fun reverseOrUpdateMuffle(mufflePoint: MufflePoint?): Int =
        withContext(Dispatchers.Default) {
            if (mufflePoint != null) {
                mufflePoint.apply {
                    return@withContext setStreamVolumes(
                        ringtoneVolume,
                        mediaVolume,
                        notificationVolume,
                        alarmVolume
                    )
                }
            } else {
                if (audioLevelStorage == null) {
                    MUFFLING_ERROR_CURRENT_VOLUME
                }

                audioLevelStorage.apply {
                    return@withContext setStreamVolumes(
                        getInt("STREAM_RING", 0),
                        getInt("STREAM_MUSIC", 0),
                        getInt("STREAM_NOTIFICATION", 0),
                        getInt("STREAM_ALARM", 0)
                    )
                }
            }
            0   // Idk why but it's needed 🤷‍♂
        }

    private fun setStreamVolumes(vararg volumes: Int): Int {
        if (volumes.size != 4) {
            return MUFFLING_ERROR_VOLUME_NOT_UPDATED
        }

        setStreamVolume(AudioManager.STREAM_RING, volumes[0])
        setStreamVolume(AudioManager.STREAM_MUSIC, volumes[1])
        setStreamVolume(AudioManager.STREAM_NOTIFICATION, volumes[2])
        setStreamVolume(AudioManager.STREAM_ALARM, volumes[3])

        return MUFFLING_SUCCESS
    }

    private fun setStreamVolume(stream: Int, volume: Int) {
        if(volume >= 0) {
            audioManager.setStreamVolume(stream, volume, 0)
        }
    }

}
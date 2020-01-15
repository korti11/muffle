package io.korti.muffle.database.entity

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.korti.muffle.R
import java.io.ByteArrayOutputStream
import java.util.*

@Entity(tableName = "muffle_point")
data class MufflePoint(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "latitude") var lat: Double = 0.0,
    @ColumnInfo(name = "longitude") var lng: Double = 0.0,
    @ColumnInfo(name = "point_radius") var radius: Double = 100.0,
    @ColumnInfo(name = "point_name") var name: String,
    @ColumnInfo(name = "point_status") var status: Int = Status.ENABLE,
    @ColumnInfo(name = "maps_image") var image: String,
    @ColumnInfo(name = "ringtone_volume") var ringtoneVolume: Int = -1,
    @ColumnInfo(name = "media_volume") var mediaVolume: Int = -1,
    @ColumnInfo(name = "notification_volume") var notificationVolume: Int = -1,
    @ColumnInfo(name = "alarm_volume") var alarmVolume: Int = -1
) {

    companion object {
        fun nameToId(name: String): String {
            return name.toLowerCase(Locale.getDefault()).replace(" ", "_")
        }

        fun bitmapToBase64(bitmap: Bitmap): String {
            var image = ByteArray(0)
            ByteArrayOutputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                image = it.toByteArray()
            }
            return Base64.encodeToString(image, Base64.DEFAULT)
        }
    }

    object Status {
        const val DISABLED = 0
        const val ENABLE = 1
        const val IN_AREA = 2
        const val ACTIVE = 3

        fun getStatus(context: Context, mufflePoint: MufflePoint) = when (mufflePoint.status) {
            DISABLED -> context.getString(R.string.muffle_point_status_disabled)
            ENABLE -> context.getString(R.string.muffle_point_status_not_active)
            IN_AREA -> context.getString(R.string.muffle_point_status_in_area)
            ACTIVE -> context.getString(R.string.muffle_point_status_active)
            else -> context.getString(R.string.muffle_point_status_unknown)
        }
    }
}
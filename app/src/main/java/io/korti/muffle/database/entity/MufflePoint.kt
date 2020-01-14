package io.korti.muffle.database.entity

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.korti.muffle.R

@Entity(tableName = "muffle_point")
data class MufflePoint(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "latitude") var lat: Double = 0.0,
    @ColumnInfo(name = "longitude") var lng: Double = 0.0,
    @ColumnInfo(name = "point_radius") var radius: Float = 100F,
    @ColumnInfo(name = "point_name") var name: String,
    @ColumnInfo(name = "point_status") var status: Int = Status.ENABLE,
    @ColumnInfo(name = "maps_image") var image: String,
    @ColumnInfo(name = "ringtone_volume") var ringtoneVolume: Int = -1,
    @ColumnInfo(name = "media_volume") var mediaVolume: Int = -1,
    @ColumnInfo(name = "notification_volume") var notificationVolume: Int = -1,
    @ColumnInfo(name = "alarm_volume") var alarmVolume: Int = -1
) {
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
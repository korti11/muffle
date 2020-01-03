package io.korti.muffle.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "muffle_point")
class MufflePoint(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "latitude") val lat: Float = 0.0F,
    @ColumnInfo(name = "longitude") val lng: Float = 0.0F,
    @ColumnInfo(name = "point_name") val name: String,
    @ColumnInfo(name = "point_enable") val enable: Boolean = true,
    @ColumnInfo(name = "point_active") val active: Boolean = false,
    @ColumnInfo(name = "maps_image") val image: String
)
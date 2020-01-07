package io.korti.muffle.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "muffle_point")
data class MufflePoint(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "latitude") var lat: Float = 0.0F,
    @ColumnInfo(name = "longitude") var lng: Float = 0.0F,
    @ColumnInfo(name = "point_radius") var radius: Float = 100F,
    @ColumnInfo(name = "point_name") var name: String,
    @ColumnInfo(name = "point_enable") var enable: Boolean = true,
    @ColumnInfo(name = "point_active") var active: Boolean = false,
    @ColumnInfo(name = "maps_image") var image: String
)
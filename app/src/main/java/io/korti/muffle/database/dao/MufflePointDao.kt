package io.korti.muffle.database.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.korti.muffle.database.entity.MufflePoint

@Dao
interface MufflePointDao {

    @Query("SELECT * FROM muffle_point WHERE uid = :mufflePointId")
    fun getById(mufflePointId: String): MufflePoint

    @Query("SELECT * FROM muffle_point")
    fun getAll(): DataSource.Factory<Int, MufflePoint>

    @Insert
    fun insertAll(vararg mufflePoints: MufflePoint)

    @Query("UPDATE muffle_point SET point_enable = :enable WHERE uid = :mufflePointId")
    fun updateEnableProperty(mufflePointId: String, enable: Boolean)

    @Delete
    fun delete(mufflePoint: MufflePoint)

}
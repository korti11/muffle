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

    @Query("SELECT * FROM muffle_point WHERE point_status = ${MufflePoint.Status.ACTIVE} LIMIT 1")
    fun getActiveMufflePoint(): MufflePoint?

    @Query("SELECT * FROM muffle_point WHERE point_status = ${MufflePoint.Status.IN_AREA} LIMIT 1")
    fun getInAreaMufflePoint(): MufflePoint

    @Query("SELECT * FROM muffle_point WHERE point_status = ${MufflePoint.Status.IN_AREA}")
    fun getInAreaMufflePoints(): List<MufflePoint>

    @Query("SELECT * FROM muffle_point WHERE point_status = ${MufflePoint.Status.ENABLE}")
    fun getEnabledMufflePoints(): List<MufflePoint>

    @Query("SELECT uid FROM muffle_point WHERE point_status = ${MufflePoint.Status.DISABLED}")
    fun getDisabled(): List<String>

    @Query("SELECT * FROM muffle_point")
    fun getAllPaged(): DataSource.Factory<Int, MufflePoint>

    @Query("SELECT * FROM muffle_point")
    fun getAll(): List<MufflePoint>

    @Insert
    fun insertAll(vararg mufflePoints: MufflePoint)

    @Query("UPDATE muffle_point SET point_status = :status WHERE uid = :mufflePointId")
    fun updateStatus(mufflePointId: String, status: Int)

    @Delete
    fun delete(mufflePoint: MufflePoint)

}
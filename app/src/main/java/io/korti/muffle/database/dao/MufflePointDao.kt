package io.korti.muffle.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.korti.muffle.database.entity.MufflePoint

@Dao
interface MufflePointDao {

    @Query("SELECT * FROM muffle_point WHERE uid = :mufflePointId")
    fun getById(mufflePointId: String): MufflePoint

    @Query("SELECT * FROM muffle_point LIMIT :limit OFFSET :offset")
    fun loadLimit(limit: Int, offset: Int): List<MufflePoint>

    @Insert
    fun insertAll(vararg mufflePoints: MufflePoint)

    @Delete
    fun delete(mufflePoint: MufflePoint)

}
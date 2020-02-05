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

package io.korti.muffle.database.dao

import androidx.paging.DataSource
import androidx.room.*
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

    @Update
    fun update(mufflePoint: MufflePoint)

    @Delete
    fun delete(mufflePoint: MufflePoint)

}
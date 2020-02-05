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

package io.korti.muffle.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import io.korti.muffle.MufflePointManager
import io.korti.muffle.database.dao.MufflePointDao
import io.korti.muffle.database.entity.MufflePoint
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    mufflePointDao: MufflePointDao,
    private val mufflePointManager: MufflePointManager
) : ViewModel() {

    private val mufflePoints: LiveData<PagedList<MufflePoint>> =
        mufflePointDao.getAllPaged().toLiveData(5)

    fun getMufflePoints(): LiveData<PagedList<MufflePoint>> {
        return mufflePoints
    }

    fun changeEnableState(mufflePoint: MufflePoint) {
        viewModelScope.launch {
            mufflePointManager.enableDisableMufflePoint(mufflePoint)
        }
    }
}
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
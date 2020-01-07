package io.korti.muffle.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import io.korti.muffle.database.dao.MufflePointDao
import io.korti.muffle.database.entity.MufflePoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(private val mufflePointDao: MufflePointDao) : ViewModel() {

    private val mufflePoints: LiveData<PagedList<MufflePoint>> =
        mufflePointDao.getAll().toLiveData(5)

    fun getMufflePoints(): LiveData<PagedList<MufflePoint>> {
        return mufflePoints
    }

    fun changeEnableState(mufflePoint: MufflePoint) {
        viewModelScope.launch {
            internalChangeEnableState(mufflePoint)
        }
    }

    private suspend fun internalChangeEnableState(mufflePoint: MufflePoint) =
        withContext(Dispatchers.IO) {
            mufflePointDao.updateEnableProperty(mufflePoint.uid, mufflePoint.enable.not())
        }
}
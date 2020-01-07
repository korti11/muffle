package io.korti.muffle.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.toLiveData
import io.korti.muffle.database.dao.MufflePointDao
import io.korti.muffle.database.entity.MufflePoint
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(mufflePointDao: MufflePointDao) : ViewModel() {

    private val mufflePoints: LiveData<PagedList<MufflePoint>> =
        mufflePointDao.getAll().toLiveData(5)

    fun getMufflePoints(): LiveData<PagedList<MufflePoint>> {
        return mufflePoints
    }

}
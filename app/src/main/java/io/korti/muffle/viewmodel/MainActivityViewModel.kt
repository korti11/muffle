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
    private val mufflePointDao: MufflePointDao,
    private val mufflePointManager: MufflePointManager
) : ViewModel() {

    private val mufflePoints: LiveData<PagedList<MufflePoint>> =
        mufflePointDao.getAllPaged().toLiveData(5)

    /*init {
        viewModelScope.launch {
            writeDebugData()
        }
    }

    private suspend fun writeDebugData() = withContext(Dispatchers.IO) {
        try {
            mufflePointDao.insertAll(
                MufflePoint(
                    "jku_universität", 48.336617, 14.319306,
                    name = "JKU Universität", image = "", ringtoneVolume = 1
                ),
                MufflePoint(
                    "home", 48.157500, 14.338056,
                    name = "Home", image = "", ringtoneVolume = 1
                )
            )
        } catch (e: SQLiteConstraintException) {
            // Just ignore it.
        }
    }*/

    fun getMufflePoints(): LiveData<PagedList<MufflePoint>> {
        return mufflePoints
    }

    fun changeEnableState(mufflePoint: MufflePoint) {
        viewModelScope.launch {
            mufflePointManager.enableDisableMufflePoint(mufflePoint)
        }
    }
}
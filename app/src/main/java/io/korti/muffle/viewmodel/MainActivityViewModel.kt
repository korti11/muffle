package io.korti.muffle.viewmodel

import android.database.sqlite.SQLiteConstraintException
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

    init {
        viewModelScope.launch {
            writeDebugData()
        }
    }

    private suspend fun writeDebugData() = withContext(Dispatchers.IO) {
        try {
            mufflePointDao.insertAll(
                MufflePoint(
                    "jku_universität", 48.336617F, 14.319306F,
                    name = "JKU Universität", image = "", ringtoneVolume = 1
                ),
                MufflePoint(
                    "home", 48.157500F, 14.338056F,
                    name = "Home", image = "", ringtoneVolume = 1
                )
            )
        } catch (e: SQLiteConstraintException) {
            // Just ignore it.
        }
    }

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
            val status = if(mufflePoint.status == MufflePoint.Status.DISABLED) {
                MufflePoint.Status.ENABLE
            } else {
                MufflePoint.Status.DISABLED
            }

            mufflePointDao.updateStatus(mufflePoint.uid, status)
        }
}
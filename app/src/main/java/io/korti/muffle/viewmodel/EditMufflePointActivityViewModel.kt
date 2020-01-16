package io.korti.muffle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.korti.muffle.database.dao.MufflePointDao
import io.korti.muffle.database.entity.MufflePoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EditMufflePointActivityViewModel @Inject constructor(private val mufflePointDao: MufflePointDao) :
    ViewModel() {

    val mufflePoint = MutableLiveData<MufflePoint>()

    fun loadMufflePoint(mufflePointId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val mufflePoint = mufflePointDao.getById(mufflePointId)
                withContext(Dispatchers.Main) {
                    this@EditMufflePointActivityViewModel.mufflePoint.value = mufflePoint
                }
            }
        }
    }

}
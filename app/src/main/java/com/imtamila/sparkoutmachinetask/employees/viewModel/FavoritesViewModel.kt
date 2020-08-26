package com.imtamila.sparkoutmachinetask.employees.viewModel

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.imtamila.sparkoutmachinetask.common.MyApplication
import com.imtamila.sparkoutmachinetask.dataBase.EmployeeDao
import com.rideo.coremodule.room.AppDatabase

class FavoritesViewModel(context: Context) :
    AndroidViewModel(context.applicationContext as MyApplication) {
    private val employeeDao: EmployeeDao by lazy {
        AppDatabase.getInstance(context).employeeListDao()
    }

    fun loadAllFavoriteContacts() = employeeDao.loadAllFavoriteContacts()
}
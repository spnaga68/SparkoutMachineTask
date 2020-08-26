package com.imtamila.sparkoutmachinetask.employees.viewModel

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.imtamila.sparkoutmachinetask.common.MyApplication
import com.imtamila.sparkoutmachinetask.dataBase.EmployeeDao
import com.imtamila.sparkoutmachinetask.employees.data.ContactData
import com.imtamila.sparkoutmachinetask.utils.runOnIoThread
import com.rideo.coremodule.room.AppDatabase
import java.util.*

class ContactsViewModel(context: Context) :
    AndroidViewModel(context.applicationContext as MyApplication) {
    internal var localContactsList: ArrayList<ContactData>? = null
    private val employeeDao: EmployeeDao by lazy {
        AppDatabase.getInstance(context).employeeListDao()
    }

    fun loadAllContacts() = employeeDao.loadAllContacts()
    fun insertAllContacts(contactsList: ArrayList<ContactData>) = runOnIoThread {
        employeeDao.insertAllContacts(contactsList)
    }

    fun searchContacts(constraint: String) = employeeDao.searchContacts(constraint)

    fun updateFavorite(newProduct: ContactData, oldProduct: ContactData) {
        runOnIoThread {
            employeeDao.updateFavorite(
                newProduct.contactName,
                newProduct.contactNumber,
                newProduct.isFavourite
            )
        }
    }
}
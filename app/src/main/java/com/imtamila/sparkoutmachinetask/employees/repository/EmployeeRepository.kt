package com.imtamila.sparkoutmachinetask.employees.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.imtamila.sparkoutmachinetask.api.ApiClient
import com.imtamila.sparkoutmachinetask.api.NetworkBoundResource
import com.imtamila.sparkoutmachinetask.common.MyApplication
import com.imtamila.sparkoutmachinetask.dataBase.EmployeeDao
import com.imtamila.sparkoutmachinetask.employees.data.EmployeeResponseData
import com.imtamila.sparkoutmachinetask.utils.AppExecutors
import com.imtamila.sparkoutmachinetask.utils.Resource
import com.rideo.coremodule.room.AppDatabase

class EmployeeRepository private constructor(val context: Context) {
    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: EmployeeRepository? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: EmployeeRepository(context).also {
                instance = it
            }
        }
    }

    private val appExecutors: AppExecutors by lazy { AppExecutors.getInstance() }
    private val employeeDao: EmployeeDao by lazy {
        AppDatabase.getInstance(context).employeeListDao()
    }

    private val apiService: ApiClient by lazy {
        MyApplication.getApiServiceWithBaseUrl(context)
    }

    fun loadUser(): LiveData<Resource<EmployeeResponseData>> {
        return object :
            NetworkBoundResource<EmployeeResponseData, EmployeeResponseData>(appExecutors) {
            override fun saveCallResult(item: EmployeeResponseData) {
                employeeDao.insert(item)
            }

            override fun shouldFetch(data: EmployeeResponseData?) = data == null

            override fun loadFromDb() = employeeDao.loadEmployees()

            override fun createCall() = apiService.getUsers()
        }.asLiveData()
    }
}
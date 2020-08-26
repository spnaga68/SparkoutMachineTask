package com.imtamila.sparkoutmachinetask.employees.viewModel

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.imtamila.sparkoutmachinetask.common.MyApplication
import com.imtamila.sparkoutmachinetask.employees.repository.EmployeeRepository

class EmployeeViewModel(private val employeeRepository: EmployeeRepository, context: Context) :
    AndroidViewModel(context.applicationContext as MyApplication) {

    internal val showLoading = MutableLiveData<Boolean>()
    val showLoader: LiveData<Boolean>
        get() = showLoading

    fun getEmployeesList() = employeeRepository.loadUser()
}
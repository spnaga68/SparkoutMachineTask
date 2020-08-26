package com.imtamila.sparkoutmachinetask.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.imtamila.sparkoutmachinetask.employees.repository.EmployeeRepository
import com.imtamila.sparkoutmachinetask.employees.viewModel.ContactsViewModel
import com.imtamila.sparkoutmachinetask.employees.viewModel.EmployeeViewModel
import com.imtamila.sparkoutmachinetask.employees.viewModel.FavoritesViewModel
import com.imtamila.sparkoutmachinetask.map.repository.MapRepository
import com.imtamila.sparkoutmachinetask.map.view.MapActivity
import com.imtamila.sparkoutmachinetask.map.viewModel.MapViewModel

class CommonViewModelFactory(private val context: Context, private val viewModelType: Int = 0) :
    ViewModelProvider.Factory {

    private fun getEmployeeRepository(context: Context): EmployeeRepository =
        EmployeeRepository.getInstance(context)

    private fun getMapRepository(context: Context): MapRepository =
        MapRepository.getInstance(context)

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (context) {
            is MapActivity -> MapViewModel(
                getMapRepository(context),
                context
            )
            else -> {
                when (viewModelType) {
                    1 -> ContactsViewModel(
                        context
                    )
                    2 -> FavoritesViewModel(
                        context
                    )
                    else -> EmployeeViewModel(
                        getEmployeeRepository(context),
                        context
                    )
                }
            }
        } as T
    }
}


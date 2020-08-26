package com.imtamila.sparkoutmachinetask

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EquipmentListViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EquipmentListViewModel(
            context
        ) as T
    }
}


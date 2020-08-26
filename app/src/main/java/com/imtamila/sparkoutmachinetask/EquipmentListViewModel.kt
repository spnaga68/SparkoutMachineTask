package com.imtamila.sparkoutmachinetask

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.imtamila.sparkoutmachinetask.api.ApiClient
import com.imtamila.sparkoutmachinetask.common.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EquipmentListViewModel(context: Context) : ViewModel() {

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoader: LiveData<Boolean>
        get() = _showLoading

    private var _equipmentResponseLiveData = MutableLiveData<EquipmentResponseData>()
    val equipmentResponseLiveData: LiveData<EquipmentResponseData>
        get() = _equipmentResponseLiveData

    private val apiService: ApiClient by lazy {
        MyApplication.getApiServiceWithBaseUrl(context)
    }

    fun getEquipmentsList() {
        _showLoading.value = true
        apiService.getEquipmentsList(
            "https://anthony.pofi5.com/json/car_home",
            EquipmentRequestData()
        )
            .enqueue(object : Callback<EquipmentResponseData> {
                override fun onFailure(call: Call<EquipmentResponseData>, t: Throwable) {
                    t.printStackTrace()
                    _showLoading.value = false
                    _equipmentResponseLiveData.value = null
                }

                override fun onResponse(
                    call: Call<EquipmentResponseData>,
                    response: Response<EquipmentResponseData>
                ) {
                    _showLoading.value = false
                    if (response.isSuccessful && response.body() != null)
                        _equipmentResponseLiveData.value = response.body() as EquipmentResponseData
                    else _equipmentResponseLiveData.value = null
                }

            })
    }
}
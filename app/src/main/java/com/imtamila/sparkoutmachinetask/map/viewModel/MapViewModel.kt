package com.imtamila.sparkoutmachinetask.map.viewModel

import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.imtamila.sparkoutmachinetask.common.MyApplication
import com.imtamila.sparkoutmachinetask.map.repository.MapRepository
import org.json.JSONObject

class MapViewModel(private val mapRepository: MapRepository, context: Context) :
    AndroidViewModel(context.applicationContext as MyApplication) {

    internal val showLoading = MutableLiveData<Boolean>()
    val showLoader: LiveData<Boolean>
        get() = showLoading

    private val routeResponse = MutableLiveData<JSONObject>()

    fun drawRoute(url: String):LiveData<JSONObject>{
        mapRepository.drawRoute(url,routeResponse)
        return routeResponse
    }
}
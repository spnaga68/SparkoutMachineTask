package com.imtamila.sparkoutmachinetask.map.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.imtamila.sparkoutmachinetask.api.ApiClient
import com.imtamila.sparkoutmachinetask.common.MyApplication
import com.imtamila.sparkoutmachinetask.dataBase.EmployeeDao
import com.imtamila.sparkoutmachinetask.utils.AppExecutors
import com.rideo.coremodule.room.AppDatabase
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapRepository private constructor(val context: Context) {
    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: MapRepository? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: MapRepository(context).also {
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

    fun drawRoute(
        url: String,
        routeResponse: MutableLiveData<JSONObject>
    ) {
        apiService.drawRoute(url).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                routeResponse.value = null
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful && response.body() != null)
                    routeResponse.value = JSONObject(response.body()!!.string())
                else routeResponse.value = null
            }

        })
    }
}
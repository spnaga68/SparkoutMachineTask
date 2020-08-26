package com.imtamila.sparkoutmachinetask.api

import androidx.lifecycle.LiveData
import com.imtamila.sparkoutmachinetask.EquipmentRequestData
import com.imtamila.sparkoutmachinetask.EquipmentResponseData
import com.imtamila.sparkoutmachinetask.employees.data.EmployeeResponseData
import com.imtamila.sparkoutmachinetask.utils.ApiResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiClient {

    @GET("users?page=1")
    fun getUsers(): LiveData<ApiResponse<EmployeeResponseData>>

    @GET
    fun drawRoute(@Url url: String): Call<ResponseBody>

    @POST
    fun getEquipmentsList(
        @Url url: String,
        @Body equipmentRequestData: EquipmentRequestData
    ): Call<EquipmentResponseData>
}
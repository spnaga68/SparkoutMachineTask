package com.imtamila.sparkoutmachinetask.dataBase

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.imtamila.sparkoutmachinetask.employees.data.EmployeeResponseData

/**
 * Type converter class to convert custom objects into string and vice-versa
 *
 * Room database supports only primitive date types like Int, String, Boolean, Long, Float and Double; Other than these should converted using @TypeConverter annotation.
 *
 */

class Converters {
    @TypeConverter
    fun fromStringToHashMap(value: String): ArrayList<EmployeeResponseData.EmployeeData> {
        val listType = object : TypeToken<ArrayList<EmployeeResponseData.EmployeeData>>() {

        }.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayListToString(employeeList: ArrayList<EmployeeResponseData.EmployeeData>): String {
        return Gson().toJson(employeeList)
    }
}
package com.imtamila.sparkoutmachinetask.employees.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employees_list")
data class EmployeeResponseData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Int,
    @ColumnInfo
    val data: ArrayList<EmployeeData>
) {
    data class EmployeeData(
        val id: Int,
        val email: String,
        val first_name: String,
        val last_name: String,
        val avatar: String
    )
}
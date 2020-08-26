package com.imtamila.sparkoutmachinetask.employees.data

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "contacts_list", primaryKeys = ["contactName", "contactNumber"])
data class ContactData(
    @ColumnInfo
    val contactName: String,
    @ColumnInfo
    val contactNumber: String,
    @ColumnInfo
    var isFavourite: Int = 0
)
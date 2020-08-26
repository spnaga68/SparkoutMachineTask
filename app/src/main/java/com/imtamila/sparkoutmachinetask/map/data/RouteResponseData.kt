package com.imtamila.sparkoutmachinetask.map.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_response_data")
data class RouteResponseData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo val id: Int
)
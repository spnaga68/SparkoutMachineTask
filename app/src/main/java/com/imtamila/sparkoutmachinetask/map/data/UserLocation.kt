package com.imtamila.sparkoutmachinetask.map.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserLocation(
    var id: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)
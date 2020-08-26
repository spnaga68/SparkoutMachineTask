package com.imtamila.sparkoutmachinetask

data class EquipmentResponseData(
    val status: Int,
    val message: String,
    val Equipment: ArrayList<EquipmentData>
)

data class EquipmentData(
    val id: Int,
    val name: String,
    val image: String,
    val category: List<CategoryData>,
    var isExpanded: Boolean = false
)

data class CategoryData(
    val id: Int,
    val name: String,
    val image: String
)

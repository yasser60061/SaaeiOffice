package com.saaei12092021.office.model.responseModel.liveSearchResponse

data class Data(
    val id: Int,
    val title: String,
    val unitNumber: Int,
    var isSelected: Boolean = false,
    var totalFloorNumber: Int = 0,
    var selectedFloorNumber: Int = 0
)
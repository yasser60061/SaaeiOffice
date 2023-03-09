package com.saaei12092021.office.model.responseModel.areasResponse

data class Area(
    val areaName: String,
    val areaName_ar: String,
    val areaName_en: String,
    val id: Int,
    val location: Location,
    var selected: Boolean = false ,
    var city: Int
)
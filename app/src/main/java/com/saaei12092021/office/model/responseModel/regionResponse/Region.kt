package com.saaei12092021.office.model.responseModel.regionResponse

data class Region(
    val id: Int,
    val location: Location,
    val regionName: String,
    val regionName_ar: String,
    val regionName_en: String
)
package com.saaei12092021.office.model.responseModel.checkPasswordResponse

data class WorkArea(
    val areaName_ar: String,
    val areaName_en: String,
    val city: Int,
    val id: Int,
    val location: List<Int>
)
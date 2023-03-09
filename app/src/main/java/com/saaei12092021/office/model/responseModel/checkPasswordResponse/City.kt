package com.saaei12092021.office.model.responseModel.checkPasswordResponse

data class City(
    val cityName_ar: String,
    val cityName_en: String,
    val country: Int,
    val id: Int,
    val img: String,
    val location: List<Int>,
    val region: Int
)
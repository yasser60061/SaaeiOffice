package com.saaei12092021.office.model.responseModel.lastContact

import java.io.Serializable

data class CityX(
    val cityName_ar: String,
    val cityName_en: String,
    val country: Int,
    val id: Int,
    val img: String,
    val location: List<Double>,
    val region: Int
):Serializable
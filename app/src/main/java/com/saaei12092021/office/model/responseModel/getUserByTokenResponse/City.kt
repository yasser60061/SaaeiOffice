package com.saaei12092021.office.model.responseModel.getUserByTokenResponse

import java.io.Serializable

data class City(
    val cityName_ar: String,
    val cityName_en: String,
    val country: Int,
    val id: Int,
    val img: String,
    val location: List<Double>,
    val priority: Int,
    val region: Int
): Serializable
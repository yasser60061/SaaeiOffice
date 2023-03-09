package com.saaei12092021.office.model.responseModel.update_password_response

data class WorkCity(
    val cityName_ar: String,
    val cityName_en: String,
    val country: Int,
    val id: Int,
    val img: String,
    val location: List<Double>
)
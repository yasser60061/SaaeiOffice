package com.saaei12092021.office.model.responseModel.update_password_response

data class Country(
    val countryCode: String,
    val countryName_ar: String,
    val countryName_en: String,
    val hint: String,
    val id: Int,
    val img: String,
    val isoCode: String,
    val numbersCount: Int
)
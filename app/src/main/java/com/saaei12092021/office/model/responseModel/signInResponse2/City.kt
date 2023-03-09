package com.saaei12092021.office.model.responseModel.signInResponse2

data class City(
    val cityName: String, // in new response only
    val cityName_ar: String,
    val cityName_en: String,
    val country: Int,
    val id: Int,
    val location: Any,
   // val location: Location,
    val region: Int ,

    val img: String, // temp for delete in old response only
)
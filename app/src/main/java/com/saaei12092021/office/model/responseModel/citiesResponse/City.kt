package com.saaei12092021.office.model.responseModel.citiesResponse

data class City(
    val cityName: String,
    val cityName_ar: String,
    val cityName_en: String,
    val id: Int,
    val img: String,
    val location: Location,
    var selected: Boolean = false,
    var selectedWork: Boolean = false
)
package com.saaei12092021.office.model.responseModel.AnoncementsResponse

data class Anoncement(
    val description: String,
    val description_ar: String,
    val description_en: String,
    val endDate: String,
    val id: Int,
    val img: String,
    val link: String,
    val linkType: String,
    val startDate: String,
    val title: String,
    val title_ar: String,
    val title_en: String
)
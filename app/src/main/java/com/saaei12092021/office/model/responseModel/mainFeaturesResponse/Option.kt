package com.saaei12092021.office.model.responseModel.mainFeaturesResponse

data class Option(
    val id: Int,
    val name: String,
    val name_ar: String,
    val name_en: String,
    var isSelected: Boolean = false,
    var thisFeatureId : Int ,
    var thisFeaturePosition : Int
)
package com.saaei12092021.office.model.responseModel.mainFeaturesResponse

data class Feature(
    val category: List<Int>,
    val createdAt: String,
    val id: Int,
    val img: String,
    val name: String,
    val name_ar: String,
    val name_en: String,
    val options: List<Option>?,
    val type: String,
    var isSelected: Boolean = false,
    var from: String = "",
    var to: String = "",
    var value: String? = "",
)
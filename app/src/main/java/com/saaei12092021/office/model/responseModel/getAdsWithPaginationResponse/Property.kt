package com.saaei12092021.office.model.responseModel.getAdsWithPaginationResponse

data class Property(
    val id: Int,
    val img: String,
    val name: String,
    val optionName: String,
    val type: String,
    val value: Int
)
package com.saaei12092021.office.model.responseModel.adRequestedByIdResponse

data class Property(
    val from: Any,
    val id: Int,
    val img: String,
    val name: String,
    val optionsName: List<String>,
    val to: Any,
    val type: String,
    val values: List<Any>
)
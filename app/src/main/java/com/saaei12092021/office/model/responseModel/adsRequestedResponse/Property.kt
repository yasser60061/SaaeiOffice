package com.saaei12092021.office.model.responseModel.adsRequestedResponse

import java.io.Serializable

data class Property(
    val from: Any,
    val id: Int,
    val img: String,
    val name: String,
    val to: Any,
    val type: String ,
    val values : ArrayList<Long> ? ,
    val optionsName : ArrayList<String> ?
): Serializable
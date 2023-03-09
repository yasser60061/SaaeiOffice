package com.saaei12092021.office.model.responseModel.adsById

import java.io.Serializable

data class Property(
    val id: Int?,
    val img: String?,
    val name: String?,
    val type: String?,
    val value: Int?,
    val optionName : String

): Serializable
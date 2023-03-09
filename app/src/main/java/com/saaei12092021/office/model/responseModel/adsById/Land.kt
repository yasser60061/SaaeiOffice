package com.saaei12092021.office.model.responseModel.adsById

import java.io.Serializable

data class Land(
    val id: Int,
    val status: String,
    val title: String,
    val unitNumber: Int
) : Serializable
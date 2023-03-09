package com.saaei12092021.office.model.responseModel.adsById

import java.io.Serializable

data class Category(
    val categoryName: String,
    val id: Int,
    val type: String
): Serializable
package com.saaei12092021.office.model.responseModel.favoritesResponse

import java.io.Serializable

data class SubCategory(
    val categoryName: String,
    val id: Int,
    val type: String
): Serializable
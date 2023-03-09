package com.saaei12092021.office.model.responseModel.getUser

import java.io.Serializable

data class WorkSubCategory(
    val categoryName: String,
    val id: Int,
    val type: String
): Serializable
package com.saaei12092021.office.model.responseModel.getUserByTokenResponse

import java.io.Serializable

data class WorkCategory(
    val categoryName_ar: String,
    val categoryName_en: String,
    val child: List<Int>,
    val createdAt: String,
    val deleted: Boolean,
    val details: String,
    val hasChild: Boolean,
    val id: Int,
    val main: Boolean,
    val priority: Int,
    val type: String
): Serializable
package com.saaei12092021.office.model.responseModel.checkPasswordResponse

data class WorkSubCategory(
    val categoryName_ar: String,
    val categoryName_en: String,
    val child: List<Any>,
    val createdAt: String,
    val deleted: Boolean,
    val details: String,
    val hasChild: Boolean,
    val id: Int,
    val kind: String,
    val main: Boolean,
    val parent: Int,
    val priority: Int,
    val type: String
)
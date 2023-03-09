package com.saaei12092021.office.model.responseModel.getAllSubCategoryResponse

data class Data(
    val categoryName: String,
    val categoryName_ar: String,
    val categoryName_en: String,
    val createdAt: String,
    val hasChild: Boolean,
    val id: Int,
    val parent: Int,
    val priority: Int,
    val type: String ,
    var isSelected : Boolean = false
)
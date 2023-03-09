package com.saaei12092021.office.model.responseModel.estateMainCategoryResponse

data class Category(
    val categoryName: String,
    val categoryName_ar: String,
    val categoryName_en: String,
    val child: ArrayList<Child>?,
    val createdAt: String,
    val hasChild: Boolean,
    val id: Int,
    val priority: Int,
    val type: String ,
    var isSelected : Boolean = false
)
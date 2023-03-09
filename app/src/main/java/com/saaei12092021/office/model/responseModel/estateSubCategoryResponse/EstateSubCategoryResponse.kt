package com.saaei12092021.office.model.responseModel.estateSubCategoryResponse

import com.google.gson.annotations.SerializedName

data class EstateSubCategoryResponse(
    @SerializedName("categories")
    val subSubCategory: List<SubCategory>?,
    val data: List<SubCategory>?,
    val success: Boolean
)
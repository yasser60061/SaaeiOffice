package com.saaei12092021.office.model.responseModel.getAllSubCategoryResponse

data class AllSubCategoryResponse(
    val data: List<Data>,
    val limit: Int,
    val links: Links,
    val page: Int,
    val pageCount: Int,
    val success: Boolean,
    val totalCount: Int
)
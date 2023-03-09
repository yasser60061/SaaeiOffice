package com.saaei12092021.office.model.responseModel.getAdsWithPaginationResponse

data class Data(
    val area: Area,
    val available: Boolean,
    val category: Category,
    val city: City,
    val contractType: String,
    val createdAt: String,
    val description: String,
    val distance: Int,
    val id: Int,
    val imgs: List<String>,
    val isFavourite: Boolean,
    val location: Location,
    val owner: Owner,
    val price: Long,
    val priceType: String,
    val properties: List<Property>,
    val rentType: String,
    val size: Long,
    val startSaleDate: Any,
    val status: String,
    val title: String,
    val unitNumber: Int,
    val updatedAt: String
)
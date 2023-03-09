package com.saaei12092021.office.model.responseModel.getAdsResponse

data class Ad(
    val area: Area,
    val city: City,
    val category: Category,
   // val subCategory: SubCategory, // لا ترجع بالريسبونس المفروض ترجع
    val contractType: String,
    val rentType : String,
    val createdAt: String,
    val description: String,
    val distance: Int,
    val id: Int,
    val imgs: List<String>? = null,
    val isFavourite: Boolean,
    val location: Location,
    val owner: Owner,
    val price: Long,
    val priceType: String,
    val properties: List<Property>,
    val size: Long,
    val startSaleDate: String,
    val status: String,
    val title: String,
    val updatedAt: String
)
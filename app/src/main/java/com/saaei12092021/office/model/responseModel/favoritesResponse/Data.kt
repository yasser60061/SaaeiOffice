package com.saaei12092021.office.model.responseModel.favoritesResponse
import com.saaei12092021.office.model.responseModel.getAdsResponse.Property

data class Data(
    val area: Area,
    val city: City,
    val contractType: String,
    val rentType: String?,
    val createdAt: String,
    val description: String,
    val distance: Int,
    val id: Int,
    val imgs: List<String>,
    val category: Category,
 //   val subCategory: SubCategory,
    val isFavourite: Boolean,
    val location: Location,
    val owner: Owner,
    val price: Any ,
    val priceType: String,
    val properties: List<Property>,
    val size: Int,
    val status: String,
    val title: String,
    val updatedAt: String
)
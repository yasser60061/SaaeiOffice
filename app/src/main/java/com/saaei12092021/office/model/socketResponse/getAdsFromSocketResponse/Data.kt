package com.saaei12092021.office.model.socketResponse.getAdsFromSocketResponse

import java.io.Serializable

data class Data(
    val area: Area,
    val city: City,
    val contractType: String,
    val rentType : String,
    val createdAt: String,
    val description: String,
    val distance: Any,
    val id: Int,
    val imgs: List<String>,
    val isFavourite: Boolean,
    val location: Location,
    val owner: Owner,
    val price: Float,
    val priceType: String,
    val properties: List<Property>,
    val size: Long ,
    val startSaleDate: String,
    val status: String,
    val title: String,
    val updatedAt: String ,
    val category : Category
) : Serializable
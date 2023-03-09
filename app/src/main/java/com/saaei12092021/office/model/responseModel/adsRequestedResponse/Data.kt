package com.saaei12092021.office.model.responseModel.adsRequestedResponse

import java.io.Serializable

data class Data(
    val area: List<Area>?,
    val city: City,
    val contractType: String,
    val rentType : String ?,
    val createdAt: String,
    val description: String,
    val id: Int,
    val owner: Owner,
    val priceFrom: Any ,
    val priceType: String ,
    val properties: List<Property> ,
    val sizeFrom: Any ,
    val sizeTo: Any ,
    val status: String,
    val title: String,
    var myId : String?,
):Serializable
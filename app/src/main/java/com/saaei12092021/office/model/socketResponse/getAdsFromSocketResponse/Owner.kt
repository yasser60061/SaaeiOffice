package com.saaei12092021.office.model.socketResponse.getAdsFromSocketResponse

import java.io.Serializable

data class Owner(
   // val city: Any,
    val fullname: String,
    val id: Int,
    val img: String,
    val online: Boolean,
    val type: String
): Serializable
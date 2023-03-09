package com.saaei12092021.office.model.socketResponse.getAdsFromSocketResponse

import java.io.Serializable

data class Property(
    val id: Int,
    val img: String,
    val name: String,
    val type: String,
    val value: Double
): Serializable
package com.saaei12092021.office.model.socketRequest

data class ViewAdsRequest(
    val relatedTo: String,
    val userId: Int,
    val viewOnId: Int
)
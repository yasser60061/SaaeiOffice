package com.saaei12092021.office.model.responseModel.notificationsResponse

data class Data(
    val createdAt: String,
    val dateMilleSec: Long,
    val deepId: Int,
    val description: String,
    val id: Int,
    val img: String,
    var read: Boolean,
    val resource: Int,
    val title: String,
    val type: String
)
package com.saaei12092021.office.model.socketRequest

data class CompleteRequestRequest(
    val comment: String,
    val contactRequest: Int,
    val lang: String,
    val office: Int,
    val rate: Float?,
    val userId: Int
)
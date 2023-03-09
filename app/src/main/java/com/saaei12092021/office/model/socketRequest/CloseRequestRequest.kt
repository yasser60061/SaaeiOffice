package com.saaei12092021.office.model.socketRequest

data class CloseRequestRequest(
    val comment: String,
    val reason : String ,
    val contactRequest: Int,
    val lang: String,
    val office: Int,
    val rate: Float?,
    val userId: Int
)
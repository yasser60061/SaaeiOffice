package com.saaei12092021.office.model.socketRequest.chatRequest

data class AcceptRequest(
    val contactRequest: Int,
    val lang: String,
    val userId: Int
)
package com.saaei12092021.office.model.socketRequest.chatRequest

data class AddContactRequest(
    val adsRequest: Int? = null,
    val lang: String,
    val sender: Int,
    val ads: Int? = null
)
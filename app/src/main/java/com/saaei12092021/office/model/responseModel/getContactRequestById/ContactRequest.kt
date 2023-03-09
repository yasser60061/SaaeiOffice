package com.saaei12092021.office.model.responseModel.getContactRequestById

data class ContactRequest(
    val ads: Int,
    val contactOn: String,
    val createdAt: String,
    val dateMilleSec: Long,
    val id: Int,
    val lastMessage: LastMessage,
    val reciever: Reciever,
    val sender: Sender,
    var status: String,
    val unReedMessage: Int
)
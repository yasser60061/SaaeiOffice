package com.saaei12092021.office.model.socketResponse.completeRequestResponse

data class Data(
    val ads: Int,
    val contactOn: String,
    val createdAt: String,
    val dateMilleSec: Long,
    val id: Int,
    val lastMessage: LastMessage,
    val reciever: Reciever,
    val sender: Sender,
    val status: String,
    val unReedMessage: Int ,
    val success: Boolean? = null,
    val errors: Any? = null,
)
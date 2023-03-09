package com.saaei12092021.office.model.responseModel.contactRequesteResponse

import java.io.Serializable

data class Data(
    val ads: Int,
    val contactOn: String,
    val createdAt: String,
    val dateMilleSec: Long,
    val id: Int,
    val reciever: Reciever,
    val sender: Sender,
    val status: String,
    val unReedMessage: Int,
    var myId : String? = "0" ,
    val lastMessage : LastMessage?
):Serializable
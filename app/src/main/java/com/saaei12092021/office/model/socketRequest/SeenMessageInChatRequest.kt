package com.saaei12092021.office.model.socketRequest

data class SeenMessageInChatRequest(
    val myId : Int ,
    val friendId : Int ,
    val contactRequest : Int
)

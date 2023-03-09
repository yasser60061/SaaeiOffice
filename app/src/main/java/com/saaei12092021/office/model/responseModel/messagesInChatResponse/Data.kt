package com.saaei12092021.office.model.responseModel.messagesInChatResponse

data class Data(
    val id: Int,
    val contactRequest: Int,
    val dataType : String ,
    val content: String,
    val createdAt: String,
    val seen: Boolean,
    val user: User
)
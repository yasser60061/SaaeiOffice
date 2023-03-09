package com.saaei12092021.office.model.responseModel

import java.io.Serializable

data class ToStartChatMainInfo(
    val contactRequest : Int ,
    val toId : Int,
    val fromId : Int ,
) : Serializable

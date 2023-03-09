package com.saaei12092021.office.model.socketRequest.chatRequest

import com.saaei12092021.office.model.responseModel.messagesInChatResponse.User
import com.saaei12092021.office.model.socketResponse.addAdsRequestFromSocketResponse.Msg
import java.io.Serializable

data class SendNewMessageRequest(
    var seen: Boolean?,
    val id: Int?,
    val content: String,
    val dataType: String,
    var createdAt: String? = null,
    val contactRequest: Int,
    val toId: Int?,
    val fromId: Int,
    var myId: Int? = null,
    val user: User?,
    var isPlayedIfAudio: Boolean = false,
    var seekarMaxIfAudio: Int = 0,
    var seekBarProgressIfAudio: Int = 0,
    val errors: List<Msg>? = null,
) : Serializable
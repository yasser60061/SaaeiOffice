package com.saaei12092021.office.model.responseModel.lastContact

import java.io.Serializable

data class Data(
    val _id: String,
    val contactRequest: Int,
    val content: String,
    val from: From,
    val incommingDate: String,
    val lastMessage: Boolean,
    val seen: Boolean,
    val to: To?,
    val unseenCount: Int
) : Serializable
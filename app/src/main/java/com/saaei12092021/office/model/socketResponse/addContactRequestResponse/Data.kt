package com.saaei12092021.office.model.socketResponse.addContactRequestResponse

import java.io.Serializable

data class Data(
    val ads: Int? = null, // the id of ads request no_ or ads no_
    val contactOn: String? = null, //ADS or ADS-REQUEST
    val createdAt: String? = null,
    val dateMilleSec: Long? = null,
    val id: Int? = null,
    val reciever: Reciever? = null,
    val sender: Sender? = null,
    val status: String? = null,
    val unReedMessage: Int? = null,
    val success: Boolean? = null,
    val errors: Any? = null,
) : Serializable
package com.saaei12092021.office.model.responseModel.contactRequesteResponse

import java.io.Serializable

data class Sender(
  //  val city: Any,
    val fullname: String,
    val id: Int,
    val img: String,
    val online: Boolean,
    val rate: Int,
    val rateCount: Int,
    val rateNumbers: Int,
    val type: String
): Serializable
package com.saaei12092021.office.model.responseModel.getUser

import java.io.Serializable

data class Parent(
    val email: String,
    val fullname: String,
    val id: Int,
    val img: String,
    val phone: String
) : Serializable
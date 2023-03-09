package com.saaei12092021.office.model.responseModel.getUser

import java.io.Serializable

data class AdsPackage(
    val defaultPackage: Boolean,
    val description: String,
    val id: Int,
    val name: String,
    val building : Boolean ,
    val plan : Boolean
): Serializable
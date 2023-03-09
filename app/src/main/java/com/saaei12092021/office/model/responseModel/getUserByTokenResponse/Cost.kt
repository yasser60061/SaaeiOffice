package com.saaei12092021.office.model.responseModel.getUserByTokenResponse

import java.io.Serializable

data class Cost(
    val cost: Int,
    val duration: Int,
    val durationType: String
): Serializable
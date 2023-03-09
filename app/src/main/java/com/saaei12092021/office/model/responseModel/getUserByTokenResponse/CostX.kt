package com.saaei12092021.office.model.responseModel.getUserByTokenResponse

import java.io.Serializable

data class CostX(
    val cost: Int,
    val duration: Int,
    val durationType: String
): Serializable
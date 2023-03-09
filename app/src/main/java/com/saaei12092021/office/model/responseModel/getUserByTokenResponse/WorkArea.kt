package com.saaei12092021.office.model.responseModel.getUserByTokenResponse

import java.io.Serializable

data class WorkArea(
    val areaName_ar: String,
    val areaName_en: String,
    val city: Int,
    val id: Int,
    val location: List<Double>
): Serializable
package com.saaei12092021.office.model.responseModel.adsById

import java.io.Serializable

data class Location(
    val coordinates: List<Double>,
    val type: String
): Serializable
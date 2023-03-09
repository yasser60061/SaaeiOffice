package com.saaei12092021.office.model.responseModel.adsById

import java.io.Serializable

data class Unit(
    val floor: Int,
    val unit: List<UnitX>
): Serializable
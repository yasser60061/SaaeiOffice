package com.saaei12092021.office.model.responseModel.adsById

import java.io.Serializable

data class Statistic(
    val city: CityXX,
    val count: Int,
    var city_percentage_for_total_views: Float = 0.0f
): Serializable
package com.saaei12092021.office.model.responseModel

import java.io.Serializable

data class BookingCalenderResponse(
    val bookingDays: List<String>
) : Serializable
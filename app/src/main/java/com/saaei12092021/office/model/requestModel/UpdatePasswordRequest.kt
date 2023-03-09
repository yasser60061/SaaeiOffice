package com.saaei12092021.office.model.requestModel

data class UpdatePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)
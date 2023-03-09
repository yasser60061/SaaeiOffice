package com.saaei12092021.office.model.socketRequest

data class WithdrawalRequest(
    val contactRequest: Int,
    val lang: String,
    val userId: Int
)
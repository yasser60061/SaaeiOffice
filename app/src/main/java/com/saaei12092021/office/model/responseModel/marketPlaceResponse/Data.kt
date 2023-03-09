package com.saaei12092021.office.model.responseModel.marketPlaceResponse

data class Data(
    val bank_iban: String,
    val brand_sector: List<String>,
    val createdAt: String,
    val entity_expiry_date: String,
    val entity_imgs: List<String>,
    val entity_number: String,
    val entity_ssuing_date: String,
    val id: Int,
    val identification_expiry_date: String,
    val identification_imgs: List<String>,
    val identification_issuing_date: String,
    val identification_type: String,
    val office: String,
    val owner_email: String,
    val owner_firstname: String,
    val owner_lastName: String,
    val owner_phone: String,
    val settlement_by: String,
    val status: String,
    val updatedAt: String
)
package com.saaei12092021.office.model.requestModel

data class AddToMarketPlaceRequest(
    val bank_iban : String,
    val brand_name_ar : String ,
    val brand_name_en : String ,
    val entity_number : String,
    val entity_ssuing_date : String,
    val entity_expiry_date : String,
    val owner_firstname : String,
    val owner_lastName : String,
    val owner_email : String,
    val owner_phone : String,
    val identification_type : String,
    val identification_issuing_date : String,
    val identification_expiry_date : String,
    val office : String,
    val tax_number : String ,

    )

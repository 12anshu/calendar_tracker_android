package com.example.smartexpensecalendar.sms_engine.merchant.model

data class MerchantEvidence(

    val source: String,

    val matchedText: String,

    val score: Int,

    val explanation: String
)
package com.example.smartexpensecalendar.sms_engine.normalizer

data class MerchantMatchResult(

    val canonicalName: String?,

    val confidence: Int,

    val matchedAlias: String? = null
)

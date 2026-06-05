package com.example.smartexpensecalendar.sms.merchant

data class MerchantMatchResult(

    val canonicalName: String?,

    val confidence: Int,

    val matchedAlias: String? = null
)
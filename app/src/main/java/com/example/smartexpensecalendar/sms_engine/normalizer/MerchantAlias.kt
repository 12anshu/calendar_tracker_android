package com.example.smartexpensecalendar.sms_engine.normalizer

data class MerchantAlias(

    val canonicalName: String,

    val aliases: Set<String>
)

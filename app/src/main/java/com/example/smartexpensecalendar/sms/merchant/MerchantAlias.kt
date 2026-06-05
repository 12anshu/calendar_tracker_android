package com.example.smartexpensecalendar.sms.merchant

data class MerchantAlias(

    val canonicalName: String,

    val aliases: Set<String>
)
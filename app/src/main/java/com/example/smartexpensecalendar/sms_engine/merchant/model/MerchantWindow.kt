package com.example.smartexpensecalendar.sms_engine.merchant.model

data class MerchantWindow(

    val text: String,

    val source: MerchantWindowSource,

    val startIndex: Int,

    val endIndex: Int
)
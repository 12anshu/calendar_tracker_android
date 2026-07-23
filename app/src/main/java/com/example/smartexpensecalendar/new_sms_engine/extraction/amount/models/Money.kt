package com.example.smartexpensecalendar.new_sms_engine.extraction.amount.models

data class Money(

    val amount: java.math.BigDecimal,

    val currency: Currency = Currency.INR,

    val rawValue: String
)
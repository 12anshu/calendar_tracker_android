package com.example.smartexpensecalendar.new_sms_engine.extraction.amount.models

enum class Currency(
    val symbol: String
) {

    INR("₹"),

    USD("$"),

    EUR("€"),

    GBP("£"),

    AED("AED"),

    UNKNOWN("");
}
package com.example.smartexpensecalendar.new_sms_engine.entity.merchant.registry

/**
 * Canonical merchant registry.
 */
object MerchantRegistry {

    private val merchants = setOf(

        "amazon",
        "flipkart",
        "swiggy",
        "zomato",
        "zepto",
        "blinkit",
        "myntra",
        "netflix",
        "spotify",
        "uber",
        "ola",
        "bookmyshow"

    )

    fun getAll(): Set<String> = merchants
}
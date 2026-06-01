package com.example.smartexpensecalendar.sms

object MerchantNormalizer {

    fun normalize(merchant: String): String {

        val lower = merchant.lowercase()

        return when {

            // Swiggy
            lower.contains("bundl") -> "swiggy"
            lower.contains("instamart") -> "swiggy"
            lower.contains("swiggy") -> "swiggy"

            // Zomato
            lower.contains("zomato") -> "zomato"

            // Amazon
            lower.contains("amazon pay") -> "amazon"
            lower.contains("amazon") -> "amazon"
            lower.contains("amzn") -> "amazon"

            // Uber
            lower.contains("uber india") -> "uber"
            lower.contains("uber") -> "uber"

            // Ola
            lower.contains("ola") -> "ola"

            // Blinkit
            lower.contains("blinkit") -> "blinkit"
            lower.contains("grofers") -> "blinkit"

            // Zepto
            lower.contains("zepto") -> "zepto"

            else -> lower.trim()
        }
    }
}
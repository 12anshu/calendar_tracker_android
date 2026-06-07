package com.example.smartexpensecalendar.sms_engine.extractor

object ExtractionUtils {

    val bankKeywords = listOf(
        "hdfc",
        "icici",
        "axis",
        "sbi",
        "kotak",
        "yes bank",
        "idfc",
        "indusind",
        "rbl",
        "federal",
        "card",
        "credit card",
        "debit card"
    )

    val merchantPrefixes = listOf(
        "RAZ*",
        "PAYTM*",
        "AMZN*",
        "AMAZON*",
        "PHONEPE*",
        "GPAY*",
        "GOOGLEPAY*",
        "SWIGGY*",
        "ZOMATO*",
        "UBER*",
        "OLA*",
        "NETFLIX*",
        "SPOTIFY*"
    )

    fun containsBankKeywords(text: String): Boolean {
        val lower = text.lowercase()
        return bankKeywords.any {
            lower.contains(it)
        }
    }

    fun containsDate(text: String): Boolean {
        return text.matches(
            ".*\\d{1,2}[-/]\\d{1,2}.*".toRegex()
        )
    }

    fun containsAmount(text: String): Boolean {
        return text.contains(
            "\\d+(\\.\\d{2})?".toRegex()
        )
    }

    fun isDateOrLimit(text: String): Boolean {
        val lower = text.lowercase()
        // Check for common date patterns like 28-May, 28/05, 2024
        val datePattern = ".*\\d{1,2}[-/](?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec|\\d{1,2}).*".toRegex()
        
        return lower.contains("limit") || 
               lower.contains("avl") || 
               lower.contains("202") || 
               lower.contains("balance") ||
               lower.matches(datePattern) ||
               text.all { it.isDigit() || it == '-' || it == '/' || it == '.' }
    }
}

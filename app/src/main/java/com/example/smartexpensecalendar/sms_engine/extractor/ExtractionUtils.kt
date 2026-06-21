package com.example.smartexpensecalendar.sms_engine.extractor

import java.util.regex.Pattern

object ExtractionUtils {

    private val bankNames = listOf(
        "HDFC", "ICICI", "AXIS", "SBI", "KOTAK", "YES BANK", "IDFC", 
        "INDUSIND", "RBL", "FEDERAL", "BARODA", "PNB", "CANARA", "DBS"
    )

    private val structuralKeywords = listOf(
        "BANK", "CARD", "A/C", "ACCOUNT", "SAVINGS", "CURRENT", "ENDING", "XX"
    )

    val merchantPrefixes = listOf(
        "RAZ*", "PAYTM*", "AMZN*", "AMAZON*", "PHONEPE*", "GPAY*", 
        "GOOGLEPAY*", "SWIGGY*", "ZOMATO*", "UBER*", "OLA*", "NETFLIX*", "SPOTIFY*"
    )

    /**
     * Checks if a string looks like a structural bank mention rather than a merchant name.
     * e.g., "HDFC Bank" or "Axis Card" or "A/c XX1234"
     */
    fun isStructuralBankMention(text: String): Boolean {
        val upper = text.uppercase()
        
        // Rule: If it's JUST a bank name (e.g., "HDFC")
        if (bankNames.any { it == upper }) return true
        
        // Rule: If it contains a bank name followed/preceded by a structural keyword
        // e.g., "HDFC Card", "Account SBI"
        val hasBank = bankNames.any { upper.contains(it) }
        val hasStructure = structuralKeywords.any { upper.contains(it) }
        
        if (hasBank && hasStructure) return true
        
        // Rule: If it's an account suffix pattern (e.g., "XX9490")
        if (upper.matches(Regex(".*[*X]{2,}\\d{2,4}.*"))) return true
        
        return false
    }

    fun containsDate(text: String): Boolean {
        return text.matches(".*\\d{1,2}[-/]\\d{1,2}.*".toRegex())
    }

    fun containsAmount(text: String): Boolean {
        return text.contains("\\d+(\\.\\d{2})?".toRegex())
    }

    fun isDateOrLimit(text: String): Boolean {
        val lower = text.lowercase()
        val datePattern = ".*\\d{1,2}[-/](?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec|\\d{1,2}).*".toRegex()
        
        return lower.contains("limit") || 
               lower.contains("avl") || 
               lower.contains("202") || 
               lower.contains("balance") ||
               lower.matches(datePattern) ||
               text.all { it.isDigit() || it == '-' || it == '/' || it == '.' }
    }
}

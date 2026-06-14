package com.example.smartexpensecalendar.sms_engine.extractor

import com.example.smartexpensecalendar.sms_engine.detector.DetectionPatterns
import java.util.regex.Pattern

object AccountNameExtractor {

    private val bankNames = listOf(
        "HDFC", "ICICI", "SBI", "AXIS", "KOTAK", "HSBC", "AMEX", "SCB", "PNB", "BOB", 
        "IDFC", "CITI", "YES BANK", "STANDARD CHARTERED", "CANARA", "DBS", "FEDERAL"
    )

    private val productTypes = listOf(
        "CREDIT CARD", "DEBIT CARD", "CARD", "ACCOUNT", "A/C", "ACCT", "MEAL CARD", "WALLET"
    )

    /**
     * Extracts a human-readable name for the source account/card.
     * e.g., "ICICI Card 9002", "DBS Account 9490", "Axis Meal Card 8801"
     */
    fun extract(body: String): String? {
        val upperBody = body.uppercase()
        
        // 1. Identify Bank/Institution
        val bank = bankNames.find { upperBody.contains(it) } ?: ""
        
        // 2. Identify Product Type
        val product = productTypes.find { upperBody.contains(it) } ?: "Account"
        
        // 3. Extract Suffix (Last 4 digits)
        val suffixRegex = Pattern.compile("(?:XX|X|\\*+)\\s*(\\d{3,4})")
        val matcher = suffixRegex.matcher(upperBody)
        val suffix = if (matcher.find()) matcher.group(1) else ""
        
        if (bank.isBlank() && suffix.isBlank()) return null
        
        return buildString {
            if (bank.isNotBlank()) append(bank.lowercase().capitalizeWords() + " ")
            append(product.lowercase().capitalizeWords())
            if (suffix.isNotBlank()) append(" " + suffix)
        }.trim()
    }

    private fun String.capitalizeWords(): String = 
        split(" ").joinToString(" ") { it.lowercase().capitalize() }
}

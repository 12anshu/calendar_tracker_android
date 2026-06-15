package com.example.smartexpensecalendar.sms_engine.extractor

import com.example.smartexpensecalendar.sms_engine.detector.DetectionPatterns
import java.util.regex.Pattern

object AccountNameExtractor {

    private val bankNames = listOf(
        // Private Sector
        "HDFC", "ICICI", "AXIS", "KOTAK", "INDUSIND", "YES BANK", "FEDERAL", "RBL", 
        "SOUTH INDIAN", "KVB", "KARUR VYSYA", "KARNATAKA BANK", "KBL", 
        "BANDHAN", "IDFC", "CITI", "HSBC", "SCB", "STANDARD CHARTERED", "AMEX", 
        "AMERICAN EXPRESS", "DEUTSCHE BANK", "DBS", "DIGIBANK",
        
        // Public Sector
        "SBI", "STATE BANK", "PNB", "PUNJAB NATIONAL", "BOB", "BARODA", "CANARA", 
        "UBI", "UNION BANK", "BOI", "BANK OF INDIA", "INDIAN BANK", "CBI", 
        "CENTRAL BANK", "UCO", "BOM", "MAHABANK", "BANK OF MAHARASHTRA", "IDBI", 
        "IOB", "INDIAN OVERSEAS", "PSB", "PUNJAB & SIND",
        
        // Small Finance & Payments
        "AU BANK", "AU SMALL", "EQUITAS", "UJJIVAN", "PAYTM", "AIRTEL", "JIO", 
        "FINO", "NSDL", "IPPB", "PAYMENTS BANK",
        
        // Co-operative & Regional
        "SARASWAT", "COSMOS", "TJSB", "SVC", "NKGSB", "COOP BANK"
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
        
        // 3. Extract Suffix (Synchronized with SMSParser robust regex)
        val suffixRegex = Pattern.compile(
            "(?i)(?:Card|Account|A/c|Acct|Acc|XX|X|No|ending|ending\\sin)\\s*[\\(\\[:\\-#\\s]*\\s*[*xX]*\\s*(\\d{2,4})\\b"
        )
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

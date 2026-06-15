package com.example.smartexpensecalendar.sms_engine.extractor

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

    /**
     * Extracts a human-readable name for the source account/card.
     * Accepts both body and sender to handle banks that omit their name from the message.
     */
    fun extract(body: String, sender: String = ""): String? {
        val upperBody = body.uppercase()
        val upperSender = sender.uppercase()
        
        // 1. Identify Bank (Check Body first, then Sender)
        var bank = bankNames.find { upperBody.contains(it) } ?: ""
        if (bank.isBlank()) {
            bank = bankNames.find { upperSender.contains(it) } ?: ""
        }
        
        // 2. Identify Product Type & Normalize to A/C
        val product = when {
            upperBody.contains("MEAL CARD") -> "MEAL CARD"
            upperBody.contains("CREDIT CARD") -> "CARD"
            upperBody.contains("DEBIT CARD") -> "CARD"
            upperBody.contains("CARD") -> "CARD"
            upperBody.contains("WALLET") -> "WALLET"
            else -> "A/C" // Default to A/C for all other account types
        }
        
        // 3. Extract Suffix (2-4 digits) - Supports "ENDING WITH", "ENDING IN", etc.
        val suffixRegex = Pattern.compile(
            "(?i)(?:Card|Account|A/c|Acct|Acc|XX|X|No|ending|ending\\s(?:in|with))\\s*[\\(\\[:\\-#\\s]*\\s*[*xX]*\\s*(\\d{2,4})\\b"
        )
        val matcher = suffixRegex.matcher(upperBody)
        val suffix = if (matcher.find()) matcher.group(1) else ""
        
        if (bank.isBlank() && suffix.isBlank()) return null
        
        return buildString {
            if (bank.isNotBlank()) append(bank + " ")
            append(product)
            if (suffix.isNotBlank()) append(" " + suffix)
        }.uppercase().trim()
    }
}

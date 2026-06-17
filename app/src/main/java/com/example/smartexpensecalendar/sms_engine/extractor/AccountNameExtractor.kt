package com.example.smartexpensecalendar.sms_engine.extractor

import com.example.smartexpensecalendar.sms.config.SenderRegistry
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

    // Improved regex to handle "Account No", "A/c ending with", etc.
    private val suffixRegex = Pattern.compile(
        "(?i)(?:Card|Account|A/c|Acct|Acc|XX|X|No|ending|ending(?:\\s(?:in|with))?\\s*No)?\\s*[\\(\\[:\\-#\\s]*\\s*[*xX]*\\s*(\\d{2,4})\\b"
    )

    /**
     * Extracts a human-readable name for the source account/card.
     */
    fun extract(body: String, sender: String = ""): String? {
        val upperBody = body.uppercase()
        
        // 1. Identify Bank (Use Sender Registry first for precision)
        var bank = getFriendlyBankName(sender) ?: ""
        
        // 2. Fallback to searching body for bank name
        if (bank.isBlank()) {
            bank = bankNames.find { upperBody.contains(it) } ?: ""
        }
        
        // 3. Identify Product Type
        val product = when {
            upperBody.contains("MEAL CARD") -> "MEAL CARD"
            upperBody.contains("CREDIT CARD") -> "CARD"
            upperBody.contains("DEBIT CARD") -> "CARD"
            upperBody.contains("CARD") -> "CARD"
            upperBody.contains("WALLET") -> "WALLET"
            else -> "A/C"
        }
        
        val suffix = getSuffix(body) ?: ""
        
        if (bank.isBlank() && suffix.isBlank()) return null
        
        return buildString {
            if (bank.isNotBlank()) append(bank + " ")
            if (suffix.isNotBlank()) {
                append("[")
                append(product)
                append(" ")
                append(suffix)
                append("]")
            } else {
                append(product)
            }
        }.uppercase().trim()
    }

    /**
     * Resolves the bank from the SMS Sender ID (Header).
     * Handles formats like JM-DBSBNK-S, AD-HDFCBK, etc.
     */
    fun getFriendlyBankName(sender: String): String? {
        if (sender.isBlank()) return null
        val upperSender = sender.uppercase()
        
        // Match the 4-8 char code between hyphens or at start/end
        val matcher = Pattern.compile("(?<=-|^)([A-Z]{4,8})(?=-|$)").matcher(upperSender)
        while (matcher.find()) {
            val code = matcher.group(1) ?: continue
            SenderRegistry.bankCodeMap[code]?.let { return it }
        }
        
        // Fallback: check if sender string contains any bank name directly (for global senders)
        return bankNames.find { upperSender.contains(it) }
    }

    fun getSuffix(body: String): String? {
        val matcher = suffixRegex.matcher(body)
        return if (matcher.find()) {
            // Group 1 is the actual digits
            matcher.group(1)
        } else null
    }
}

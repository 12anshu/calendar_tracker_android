package com.example.smartexpensecalendar.sms

import java.util.regex.Pattern

data class ParsedSMS(
    val amount: Double,
    val merchant: String?,
    val isFinancial: Boolean
)

object SMSParser {
    // Regex for amount: Rs 250, INR 1200, ₹450, 450.00, Rs. 300, INR300
    private val amountRegex = Pattern.compile(
        "(?i)(?:RS\\.?|INR|₹|Rs\\.?)\\s*([\\d,]+(?:\\.\\d{1,2})?)",
        Pattern.CASE_INSENSITIVE
    )

    // Keywords to detect financial messages
    private val financialKeywords = listOf(
        "spent", "debited", "paid", "transaction", "successful", "order", "payment", "txn", "autopay"
    )

    // Keywords to ignore
    private val ignoreKeywords = listOf(
        "otp", "verification", "code", "password", "login", "received", "credited", "is due", 
        "statement", "failed", "declined", "limit", "available balance", "avl limit"
    )

    fun parse(body: String): ParsedSMS? {
        val lowercaseBody = body.lowercase()

        // 1. Basic check if it's financial and NOT an OTP/credit
        if (ignoreKeywords.any { lowercaseBody.contains(it) && !it.startsWith("avl") }) {
             // Exception: "spent" or "debited" can coexist with "limit"
             if (!(lowercaseBody.contains("spent") || lowercaseBody.contains("debited") || lowercaseBody.contains("txn"))) return null
        }
        
        if (!financialKeywords.any { lowercaseBody.contains(it) }) return null

        // 2. Extract Amount
        val amountMatcher = amountRegex.matcher(body)
        if (!amountMatcher.find()) return null
        
        val amountStr = amountMatcher.group(1)?.replace(",", "") ?: return null
        val amount = amountStr.toDoubleOrNull() ?: return null

        // 3. Extract Merchant
        val merchant = extractMerchant(body)

        return ParsedSMS(amount, merchant, true)
    }

    private fun extractMerchant(body: String): String? {
        val lines = body.split("\n").map { it.trim() }.filter { it.isNotBlank() }
        
        // Pattern 1: Multi-line (Axis Bank style)
        // Line 1: Spent INR ...
        // Line 4: Merchant
        if (lines.size >= 4 && lines[0].contains("Spent", ignoreCase = true) && lines[1].contains("Bank", ignoreCase = true)) {
            val potentialMerchant = lines[3]
            if (!potentialMerchant.contains("Limit", ignoreCase = true)) {
                return cleanMerchant(potentialMerchant)
            }
        }

        val patterns = listOf(
            "(?i)spent on ([a-zA-Z0-9 *._@]+)",
            "(?i)paid to ([a-zA-Z0-9 *._@]+)",
            "(?i)at ([a-zA-Z0-9 *._@]+)",
            "(?i)via ([a-zA-Z0-9 *._@]+)",
            "(?i)on ([a-zA-Z0-9 *._@]+)",
            "(?i)For ([a-zA-Z0-9 *._@]+)",
            "(?i)to ([a-zA-Z0-9 *._@]+)",
            "(?i)@UPI_([a-zA-Z0-9 *._@]+)"
        )

        for (patternStr in patterns) {
            val pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(body)
            if (matcher.find()) {
                val rawMerchant = matcher.group(1)?.trim()
                if (!rawMerchant.isNullOrBlank()) {
                    // Avoid taking date or limit as merchant
                    val cleaned = cleanMerchant(rawMerchant)
                    if (cleaned != null && !isDateOrLimit(cleaned)) {
                        return cleaned
                    }
                }
            }
        }
        
        return null
    }

    private fun cleanMerchant(merchant: String): String? {
        var cleaned = merchant.replace("(?i)RAZ\\*|UPI_|WWW|\\.COM|\\.IN|\\.NET|PRIV|LTD|DIGITAL|MARKETPLACE|MARKETPLA|TECHN".toRegex(), " ")
            .replace("[._*@#]".toRegex(), " ")
            .trim()
            .split(" ")
            .filter { it.length > 1 }
            .take(3)
            .joinToString(" ")
            
        if (cleaned.isBlank()) return null
        return cleaned
    }

    private fun isDateOrLimit(text: String): Boolean {
        val lower = text.lowercase()
        return lower.contains("limit") || lower.contains("avl") || lower.contains("202") || lower.contains("balance")
    }
}

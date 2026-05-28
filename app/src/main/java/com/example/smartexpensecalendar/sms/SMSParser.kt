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
        "spent", "debited", "paid", "transaction", "successful", "order", "payment", "txn", "using card", "on card", "purchase"
    )

    // Keywords to ignore (Strict)
    private val ignoreKeywords = listOf(
        "otp", "verification", "code", "password", "login", "received", "credited", 
        "total amount due", "minimum amount due", "ignore if paid", "is due", 
        "statement", "failed", "declined", "limit", "available balance", "avl limit",
        "cheq", "repayment", "repaid", "will be auto debited", "autopay facility", 
        "to deactivate", "balance is low", "low balance", "recharge of"
    )

    fun parse(body: String): ParsedSMS? {
        val lowercaseBody = body.lowercase()

        // 1. Strict Filter for Non-Transaction Alerts
        if (ignoreKeywords.any { lowercaseBody.contains(it) }) {
            // Exceptions: "spent" or "debited" can coexist with "limit" (balance info), 
            // but NOT with most ignore keywords
            val hasStrongFinancial = lowercaseBody.contains("spent") || lowercaseBody.contains("debited")
            val isHardIgnore = lowercaseBody.contains("total amount due") || 
                              lowercaseBody.contains("minimum amount due") ||
                              lowercaseBody.contains("will be") ||
                              lowercaseBody.contains("otp") ||
                              lowercaseBody.contains("recharge of")
            
            if (isHardIgnore || !hasStrongFinancial) return null
        }
        
        if (!financialKeywords.any { lowercaseBody.contains(it) }) return null

        // 2. Extract Amount
        val amountMatcher = amountRegex.matcher(body)
        if (!amountMatcher.find()) return null
        
        val amountStr = amountMatcher.group(1)?.replace(",", "") ?: return null
        val amount = amountStr.toDoubleOrNull() ?: return null

        // 3. Extract Merchant with Fallback to Bank Source
        var merchant = extractMerchant(body)
        
        // Handle NEFT / ACH / Bank-as-Merchant cases
        if (merchant == null || merchant.contains("Bank", ignoreCase = true)) {
            merchant = identifyBankSource(body)
        }

        return ParsedSMS(amount, merchant, true)
    }

    private fun identifyBankSource(body: String): String {
        return when {
            body.contains("HDFC", ignoreCase = true) -> "HDFC Bank"
            body.contains("Axis", ignoreCase = true) -> "Axis Bank"
            body.contains("ICICI", ignoreCase = true) -> "ICICI Bank"
            body.contains("SBI", ignoreCase = true) -> "SBI Bank"
            else -> "Bank Transaction"
        }
    }

    private fun extractMerchant(body: String): String? {
        // Special check for NEFT/ACH
        if (body.contains("NEFT", ignoreCase = true) || body.contains("ACH", ignoreCase = true)) {
            val type = if (body.contains("NEFT", ignoreCase = true)) "NEFT" else "ACH"
            return "${identifyBankSource(body)} ($type)"
        }

        // Handle Meal Card
        if (body.contains("Meal Card", ignoreCase = true)) {
            return "Meal Card Transaction"
        }

        val patterns = listOf(
            "(?i)at ([a-zA-Z0-9 *._@]+) on",
            "(?i)at ([a-zA-Z0-9 *._@]+)\\.",
            "(?i)spent on ([a-zA-Z0-9 *._@]+)",
            "(?i)paid to ([a-zA-Z0-9 *._@]+)",
            "(?i)via ([a-zA-Z0-9 *._@]+)",
            "(?i)on ([a-zA-Z0-9 *._@]+)",
            "(?i)For ([a-zA-Z0-9 *._@]+)",
            "(?i)to ([a-zA-Z0-9 *._@]+)"
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

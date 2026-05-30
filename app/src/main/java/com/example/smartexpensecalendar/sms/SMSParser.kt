package com.example.smartexpensecalendar.sms

import java.util.regex.Pattern

data class ParsedSMS(
    val amount: Double,
    val merchant: String?,
    val isFinancial: Boolean,
    val type: com.example.smartexpensecalendar.domain.model.TransactionType = com.example.smartexpensecalendar.domain.model.TransactionType.DEBIT,
    val status: com.example.smartexpensecalendar.domain.model.TransactionStatus = com.example.smartexpensecalendar.domain.model.TransactionStatus.COMPLETED,
    val accountSuffix: String? = null
)

object SMSParser {
    // Regex for amount: Rs 250, INR 1200, ₹450, 450.00, Rs. 300, INR300, USD 5.90
    private val amountRegex = Pattern.compile(
        "(?i)(?:RS\\.?|INR|₹|Rs\\.?|USD|GBP|EUR)\\s*([\\d,]+(?:\\.\\d{1,2})?)",
        Pattern.CASE_INSENSITIVE
    )

    // Regex for Account/Card Suffix (Last 4 digits)
    private val accountSuffixRegex = Pattern.compile(
        "(?i)(?:A/c|Acct|Card|XX|X|No|no\\.?)\\s*\\*?([0-9]{4})",
        Pattern.CASE_INSENSITIVE
    )

    // Keywords to detect financial messages
    private val financialKeywords = listOf(
        "spent", "debited", "paid", "transaction", "successful", "order", "payment", "txn", 
        "using card", "on card", "purchase", "sent", "transferred", "received", "refunded", "credited"
    )

    // Settlement keywords
    private val settlementKeywords = listOf("payment received on", "bill payment", "cc payment", "online payment", "credited to your card")
    
    // Refund/Failed keywords
    private val refundKeywords = listOf("refund", "reversed", "credited back", "failed")

    // Keywords to ignore (Strict)
    private val ignoreKeywords = listOf(
        "otp", "verification", "code", "password", "login", 
        "total amount due", "minimum amount due", "ignore if paid", "is due", 
        "statement", "limit", "available balance", "avl limit",
        "cheq", "repayment", "repaid", "will be auto debited", "autopay facility", 
        "to deactivate", "balance is low", "low balance", "recharge of"
    )

    fun parse(body: String): ParsedSMS? {
        val lowercaseBody = body.lowercase()

        // 1. Strict Filter for Non-Transaction Alerts
        if (ignoreKeywords.any { lowercaseBody.contains(it) }) {
            // Exceptions: "spent" or "debited" or "received" can coexist with "limit" (balance info), 
            // but NOT with most ignore keywords
            val hasStrongFinancial = lowercaseBody.contains("spent") || 
                                   lowercaseBody.contains("debited") ||
                                   lowercaseBody.contains("received")
            
            val isHardIgnore = lowercaseBody.contains("total amount due") || 
                              lowercaseBody.contains("minimum amount due") ||
                              lowercaseBody.contains("will be") ||
                              lowercaseBody.contains("otp") ||
                              lowercaseBody.contains("recharge of")
            
            if (isHardIgnore || !hasStrongFinancial) return null
        }
        
        if (!financialKeywords.any { lowercaseBody.contains(it) }) return null

        // 1.5 Special Account Exception: HDFC XX6038
        if (lowercaseBody.contains("6038")) {
            val isKeepTransaction = lowercaseBody.contains("emi") || 
                                   lowercaseBody.contains("cash") || 
                                   lowercaseBody.contains("withdrawal") ||
                                   lowercaseBody.contains("ach")
            
            // Exclude if it's a transfer to the specific internal account (9490)
            if (lowercaseBody.contains("9490")) return null
            
            // Generally exclude this account unless it's a whitelisted expense type
            if (!isKeepTransaction) return null
        }

        // 2. Extract Amount and Currency
        val amountMatcher = amountRegex.matcher(body)
        var foundAmount = 0.0
        var foundMatch = false
        
        // Strategy: The first occurrence of a currency symbol + amount in a financial message 
        // is usually the transaction amount, while the second (if any) is the balance.
        while (amountMatcher.find()) {
            val amountStr = amountMatcher.group(1)?.replace(",", "")
            val amount = amountStr?.toDoubleOrNull() ?: continue
            
            // We take the first match that isn't preceded by "Avl Limit" or "Balance"
            val matchStart = amountMatcher.start()
            val textBefore = body.substring(maxOf(0, matchStart - 20), matchStart).lowercase()
            
            if (textBefore.contains("limit") || textBefore.contains("bal")) {
                continue
            }
            
            foundAmount = amount
            foundMatch = true
            
            // Convert USD/EUR to approximate INR if needed, or just keep the number for now
            // For now, let's just ensure we pick the correct one.
            break 
        }

        if (!foundMatch) return null

        val finalAmount = foundAmount

        // 3. Determine Type and Status
        var type = com.example.smartexpensecalendar.domain.model.TransactionType.DEBIT
        var status = com.example.smartexpensecalendar.domain.model.TransactionStatus.COMPLETED

        if (settlementKeywords.any { lowercaseBody.contains(it) }) {
            status = com.example.smartexpensecalendar.domain.model.TransactionStatus.SETTLEMENT
            type = com.example.smartexpensecalendar.domain.model.TransactionType.CREDIT
        } else if (refundKeywords.any { lowercaseBody.contains(it) }) {
            status = if (lowercaseBody.contains("failed")) 
                com.example.smartexpensecalendar.domain.model.TransactionStatus.FAILED 
            else 
                com.example.smartexpensecalendar.domain.model.TransactionStatus.REFUNDED
            type = com.example.smartexpensecalendar.domain.model.TransactionType.CREDIT
        } else if (lowercaseBody.contains("received") || lowercaseBody.contains("credited")) {
            // Generic received (might be Salary or P2P)
            type = com.example.smartexpensecalendar.domain.model.TransactionType.CREDIT
        }

        // 4. Extract Account Suffix
        val suffixMatcher = accountSuffixRegex.matcher(body)
        var accountSuffix: String? = null
        if (suffixMatcher.find()) {
            accountSuffix = suffixMatcher.group(1)
        }

        // 5. Extract Merchant
        var merchant = extractMerchant(body)
        
        // Handle NEFT / ACH / Bank-as-Merchant cases
        if (merchant == null || merchant.contains("Bank", ignoreCase = true)) {
            merchant = identifyBankSource(body)
        }

        return ParsedSMS(finalAmount, merchant, true, type, status, accountSuffix)
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

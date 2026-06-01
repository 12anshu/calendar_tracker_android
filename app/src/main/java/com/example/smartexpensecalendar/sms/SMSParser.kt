package com.example.smartexpensecalendar.sms

import com.example.smartexpensecalendar.domain.model.TransactionStatus
import com.example.smartexpensecalendar.domain.model.TransactionType
import java.util.regex.Pattern

data class ParsedSMS(
    val amount: Double,
    val merchant: String?,
    val isFinancial: Boolean,
    val paymentMethod: String? = null,
    val type: TransactionType = TransactionType.DEBIT,
    val status: TransactionStatus = TransactionStatus.COMPLETED,
    val accountSuffix: String? = null
)
object SMSParser {

    // Bank Keywords
    private val bankKeywords = listOf(
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

    private val merchantPrefixes = listOf(
        "RAZ*",
        "PAYTM*",
        "AMZN*",
        "AMAZON*",
        "PHONEPE*",
        "GPAY*",
        "GOOGLEPAY*"
    )
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
        var type = TransactionType.DEBIT
        var status = TransactionStatus.COMPLETED

        if (settlementKeywords.any { lowercaseBody.contains(it) }) {
            status = TransactionStatus.SETTLEMENT
            type = TransactionType.CREDIT
        } else if (refundKeywords.any { lowercaseBody.contains(it) }) {
            status = if (lowercaseBody.contains("failed")) 
                TransactionStatus.FAILED
            else 
                TransactionStatus.REFUNDED
            type = TransactionType.CREDIT
        } else if (lowercaseBody.contains("received") || lowercaseBody.contains("credited")) {
            // Generic received (might be Salary or P2P)
            type = TransactionType.CREDIT
        }

        // 4. Extract Account Suffix
        val suffixMatcher = accountSuffixRegex.matcher(body)
        var accountSuffix: String? = null
        if (suffixMatcher.find()) {
            accountSuffix = suffixMatcher.group(1)
        }

        // 5. Extract Merchant
        var merchant = extractMerchant(body)
        
        // If the merchant extracted is just a bank name, we should try again or null it 
        // to let the bank fallback logic handle it more cleanly or keep searching.
        if (merchant != null && isJustBankName(merchant)) {
            merchant = null
        }

        // Handle NEFT / ACH / Bank-as-Merchant cases
        val paymentMethod = detectPaymentMethod(body)

        return ParsedSMS(
            amount = finalAmount,
            merchant = merchant,
            isFinancial = true,
            paymentMethod = paymentMethod,
            type = type,
            status = status,
            accountSuffix = accountSuffix
        )
    }

    private fun detectPaymentMethod(body: String): String? {

        return when {

            body.contains("upi", true) -> "UPI"

            body.contains("credit card", true) -> "Credit Card"

            body.contains("debit card", true) -> "Debit Card"

            body.contains("card", true) -> "Card"

            body.contains("neft", true) -> "NEFT"

            body.contains("imps", true) -> "IMPS"

            body.contains("rtgs", true) -> "RTGS"

            body.contains("ach", true) -> "ACH"

            else -> null
        }
    }

    private fun isJustBankName(name: String): Boolean {
        val banks = listOf("ICICI", "HDFC", "SBI", "AXIS", "KOTAK", "PNB", "YES BANK", "IDFC")
        return banks.any { name.equals(it, ignoreCase = true) || name.equals("$it Bank", ignoreCase = true) }
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

        extractUPIMerchant(body)?.let { return it }

        extractMerchantFromLines(body)?.let { return it }

        extractMerchantFromPatterns(body)?.let { return it }

        extractMerchantFromVPA(body)?.let { return it }

        return null
    }

    private fun extractUPIMerchant(body: String): String? {

        val pattern = Pattern.compile(
            "@UPI[_ ]+([A-Za-z0-9 ]+)",
            Pattern.CASE_INSENSITIVE
        )

        val matcher = pattern.matcher(body)

        if (matcher.find()) {

            val merchant = matcher.group(1)
                ?.replace("\\b\\d+\\b$".toRegex(), "")
                ?.trim()

            return cleanMerchant(merchant)
        }

        return null
    }

    private fun extractMerchantFromLines(
        body: String
    ): String? {

        val lines = body.lines()

        for (line in lines) {

            val text = line.trim()

            if (text.isBlank())
                continue

            if (merchantPrefixes.any {
                    text.startsWith(it, true)
                }) {

                return cleanMerchant(text)
            }

            if (
                text.length in 4..40 &&
                !containsBankKeywords(text) &&
                !containsDate(text) &&
                !containsAmount(text)
            ) {

                if (
                    text.contains("swiggy", true) ||
                    text.contains("zomato", true) ||
                    text.contains("amazon", true) ||
                    text.contains("flipkart", true) ||
                    text.contains("bundl", true)
                ) {

                    return cleanMerchant(text)
                }
            }
        }

        return null
    }

    private fun extractMerchantFromPatterns(
        body: String
    ): String? {

        val patterns = listOf(

            "(?i)at (.+?)(?: on| via|\\.|,|$)",

            "(?i)spent at (.+?)(?: on| via|\\.|,|$)",

            "(?i)paid to (.+?)(?: on| via|\\.|,|$)",

            "(?i)sent to (.+?)(?: on| via|\\.|,|$)",

            "(?i)merchant[: ]+(.+?)(?:\\.|,|$)"
        )

        for (patternStr in patterns) {

            val matcher =
                Pattern.compile(patternStr).matcher(body)

            while (matcher.find()) {

                val cleaned =
                    cleanMerchant(matcher.group(1))

                if (
                    cleaned != null &&
                    !isDateOrLimit(cleaned) &&
                    !containsBankKeywords(cleaned)
                ) {
                    return cleaned
                }
            }
        }

        return null
    }

    private fun extractMerchantFromVPA(
        body: String
    ): String? {

        val matcher = Pattern.compile(
            "\\b([a-zA-Z0-9._-]+)@([a-zA-Z]+)\\b"
        ).matcher(body)

        if (matcher.find()) {

            val handle =
                matcher.group(1)?.lowercase() ?: return null

            if (
                handle.contains("swiggy") ||
                handle.contains("zomato") ||
                handle.contains("amazon") ||
                handle.contains("uber")
            ) {

                return cleanMerchant(handle)
            }
        }

        return null
    }

    private fun cleanMerchant(
        merchant: String?
    ): String? {

        if (merchant.isNullOrBlank())
            return null

        val cleaned = merchant

            .replace("(?i)RAZ\\*".toRegex(), "")
            .replace("(?i)PAYTM\\*".toRegex(), "")
            .replace("(?i)AMZN".toRegex(), "Amazon")
            .replace("(?i)BUNDL".toRegex(), "Swiggy")

            .replace("(?i)TECHNOLOGIES".toRegex(), "")
            .replace("(?i)INSTAMART".toRegex(), "")
            .replace("(?i)PRIVATE".toRegex(), "")
            .replace("(?i)PVT".toRegex(), "")
            .replace("(?i)LIMITED".toRegex(), "")
            .replace("(?i)LTD".toRegex(), "")

            .replace("[._*@#]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()

        return cleaned
            .split(" ")
            .take(3)
            .joinToString(" ")
            .ifBlank { null }
    }

    private fun containsBankKeywords(text: String): Boolean {

        val lower = text.lowercase()

        return bankKeywords.any {
            lower.contains(it)
        }
    }

    private fun containsDate(text: String): Boolean {

        return text.matches(
            ".*\\d{1,2}[-/]\\d{1,2}.*".toRegex()
        )
    }

    private fun containsAmount(text: String): Boolean {

        return text.contains(
            "\\d+(\\.\\d{2})?".toRegex()
        )
    }
    private fun isDateOrLimit(text: String): Boolean {
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

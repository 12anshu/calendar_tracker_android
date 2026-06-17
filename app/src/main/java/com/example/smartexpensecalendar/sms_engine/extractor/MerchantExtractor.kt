package com.example.smartexpensecalendar.sms_engine.extractor

import com.example.smartexpensecalendar.sms_engine.normalizer.MerchantNormalizer
import java.util.regex.Pattern

object MerchantExtractor {

    fun extractMerchant(
        smsText: String
    ): String? {

        extractNEFTMerchant(smsText)?.let {
            return it
        }

        extractUPIMerchant(smsText)?.let {
            return it
        }

        extractMerchantFromLines(smsText)?.let {
            return it
        }

        extractMerchantFromPatterns(smsText)?.let {
            return it
        }

        extractMerchantFromVPA(smsText)?.let {
            return it
        }

        return null
    }

    // copy remaining methods
    private fun extractNEFTMerchant(body: String): String? {
        val patterns = listOf(
            Pattern.compile("NEFT\\s+Cr-[A-Z0-9]+-([A-Z0-9\\s]+?)(?=\\s+-|\\s+\\.|-|$)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("IMPS-[0-9]+-([A-Z0-9\\s]+?)(?=\\s+-|\\s+\\.|-|$)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("NEFT\\s+INWARD-[A-Z0-9]+-([A-Z0-9\\s]+?)(?=\\s+-|\\s+\\.|-|$)", Pattern.CASE_INSENSITIVE)
        )

        for (pattern in patterns) {
            val matcher = pattern.matcher(body)
            if (matcher.find()) {
                val merchant = matcher.group(1)?.trim()
                if (!merchant.isNullOrBlank()) {
                    return normalizeMerchant(cleanMerchant(merchant))
                }
            }
        }
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

            return normalizeMerchant(cleanMerchant(merchant))
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

            if (ExtractionUtils.merchantPrefixes.any {
                    text.startsWith(it, true)
                }) {

                return normalizeMerchant(cleanMerchant(text))
            }

            if (
                text.length in 4..40 &&
                !ExtractionUtils.containsBankKeywords(text) &&
                !ExtractionUtils.containsDate(text) &&
                !ExtractionUtils.containsAmount(text)
            ) {

                if (
                    !ExtractionUtils.containsAmount(text) && !ExtractionUtils.containsDate(text) && !ExtractionUtils.containsBankKeywords(text) && text.any { it.isLetter() }
                ) {
                    return normalizeMerchant(cleanMerchant(text))
                }
            }
        }

        for (line in lines) {

            val text = line.trim()

            if (
                text.length in 4..40 &&
                text.matches(
                    Regex("^[A-Z0-9 _.-]+$")
                )
            ) {
                if (looksLikeMerchant(text)) {
                    return normalizeMerchant(cleanMerchant(text))
                }
            }
        }

        return null
    }

    private fun extractMerchantFromPatterns(
        body: String
    ): String? {

        val patterns = listOf(
            "(?i)\\bon\\s+([A-Za-z][A-Za-z0-9 .&@_\\-]{2,50}?)(?:\\.|,|\\s+via|\\s+using|\\s+for|$)",
            "(?i)At\\s+([A-Za-z0-9@._\\-]+?)\\s+by UPI",

            "(?i)at\\s+\\.\\.([A-Za-z0-9 _\\-]+?)_\\s+on",

            "(?i)at (.+?)(?: on| via|\\.|,|$)",

            "(?i)spent using .*? card .*? on .*? on ([A-Za-z0-9* _.-]+?)(?:\\.| Avl|$)",

            "(?i)on\\s+\\d{2}-[A-Za-z]{3}-\\d{2}\\s+on\\s+([A-Za-z0-9* _.-]+?)(?:\\.| Avl|$)",

            "(?i)purchased at ([A-Za-z0-9* _.-]+?)(?: on| via|\\.|,|$)",

            "(?i)info[: ]+([A-Za-z0-9* _.-]+?)(?: on| via|\\.|,|$)",

            "(?i)spent at (.+?)(?: on| via|\\.|,|$)",

            "(?i)paid to (.+?)(?: on| via|\\.|,|$)",

            // --- TRANSFER PAYEES (Deep Research Patterns) ---
            "(?i)to\\s+([A-Za-z][A-Za-z0-9 .]{2,30}?)\\s+via\\s+(?:NEFT|IMPS|RTGS|UPI|BANK|TRANSFER)",
            "(?i)transferred to\\s+([A-Za-z][A-Za-z0-9 .]{2,30}?)(?:\\s+via|\\s+Ref|\\.|\\s+on|$)",
            "(?i)to pay\\s+([A-Za-z][A-Za-z0-9 .]{2,30}?)(?:\\.|\\s+The|\\s+Ref|$)",
            "(?i)towards\\s+([A-Za-z][A-Za-z0-9 .]{2,30}?)(?:\\.|\\s+Ref|\\s+via|$)",
            "(?i)beneficiary\\s+([A-Za-z][A-Za-z0-9 .]{2,30}?)(?:\\s+is|\\.|\\s+Ref|$)",
            "(?i)remit to\\s+([A-Za-z][A-Za-z0-9 .]{2,30}?)(?:\\.|\\s+via|$)",
            "(?i)payment made to\\s+([A-Za-z][A-Za-z0-9 .]{2,30}?)(?:\\.|\\s+Ref|$)",
            "(?i)sent to\\s+([A-Za-z][A-Za-z0-9 .]{2,30}?)(?:\\.|\\s+on|$)",

            "(?i)merchant[: ]+(.+?)(?:\\.|,|$)",

            "(?i)for (.+?)(?: on| via|\\.|,|$)",

            "(?i)for\\s+([A-Za-z0-9 ._\\-]+?)\\s*(?:\\n|txn|dt:|via:|$)",

            "(?i)At\\s+(.+?)\\s*(?:\\n|by\\s+upi|on\\s+\\d|$)"
        )

        for (patternStr in patterns) {

            val matcher =
                Pattern.compile(patternStr).matcher(body)

            while (matcher.find()) {

                val cleaned = cleanMerchant(matcher.group(1))

                if (
                    cleaned != null &&
                    looksLikeMerchant(cleaned)
                ) {
                    return normalizeMerchant(cleaned)
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

                return normalizeMerchant(cleanMerchant(handle))
            }
        }

        return null
    }

    private val invalidMerchantPhrases = setOf(
        "neft txn",
        "via neft",
        "bill payment system",
        "is successfully credited",
        "referral benefits",
        "transaction successful",
        "credited",
        "debited",
        "https",
        "payment received",
        "otp for",
        "bank account",
        "account no",
        "account ending",
        "a/c no",
        "card ending",
        "available limit",
        "not you?",
        "call",
        "any assistance",
        "emi facility",
        "balance transfer",
        "credit card",
        "statement generated",
        "usage settings",
        "airport lounge",
        "reward points",
        "cashback offer",
        "monthly statement",
        "amount due",
        "statement",
        "statement generated",
        "cashback",
        "payment due",
        "minimum amount"
    )

    private fun cleanMerchant(
        merchant: String?
    ): String? {

        if (merchant.isNullOrBlank())
            return null

        if (isInvalidMerchant(merchant)) {
            return null
        }

        val cleaned = merchant

            .replace("(?i)^at ".toRegex(), "")
            .replace("(?i)^paid to ".toRegex(), "")
            .replace("(?i)^sent to ".toRegex(), "")
            .replace("(?i)^merchant[: ]+".toRegex(), "")
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
            .replace("(?i)CORP".toRegex(), "")
            .replace("(?i)SOLUTIONS".toRegex(), "")
            .replace("(?i)SERVICES".toRegex(), "")
            .replace("(?i)ENTERPRISES".toRegex(), "")
            .replace("(?i)RETAIL".toRegex(), "")
            .replace("(?i)INDIA".toRegex(), "")
            .replace("(?i)MARKETING".toRegex(), "")
            .replace("(?i)COMMERCE".toRegex(), "")
            .replace("(?i)TRADING".toRegex(), "")

            .replace("[._*@#]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")

            .replace("(?i)ZOMATO PAY".toRegex(), "Zomato")
            .replace("(?i)SWIGGY LIMITED".toRegex(), "Swiggy")
            .replace("(?i)UBER INDIA".toRegex(), "Uber")

            .replace("^\\.+".toRegex(), "")
            .replace("\\.+$".toRegex(), "")
            .replace("_+$".toRegex(), "")
            .replace("\\d+$".toRegex(), "")

            .replace("^\\.+".toRegex(), "")
            .replace("_+$".toRegex(), "")
            .replace("\\.{2,}".toRegex(), "")

            .replace("_".toRegex(), " ")
            .replace("(?i)^Q\\d+@YBL$".toRegex(), "Unknown UPI Merchant")
            .replace("(?i)^PAYTM.*@PTYS$".toRegex(), "Paytm")

            .trim()

        return cleaned
            .split(" ")
            .take(3)
            .joinToString(" ") { word ->
                word.lowercase().replaceFirstChar { it.uppercase() }
            }
            .ifBlank { null }
    }

    private fun isInvalidMerchant(
        merchant: String
    ): Boolean {

        val value = merchant.lowercase()

        return invalidMerchantPhrases.any {
            value.contains(it)
        }
    }

    private fun normalizeMerchant(
        merchant: String?
    ): String? {
        return MerchantNormalizer.normalize(merchant)
    }

    private fun looksLikeMerchant(text: String): Boolean {
        return !ExtractionUtils.containsAmount(text) &&
                !ExtractionUtils.containsDate(text) &&
                !ExtractionUtils.containsBankKeywords(text) &&
                text.length in 3..50
    }

}
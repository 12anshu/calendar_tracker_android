package com.example.smartexpensecalendar.sms

object SMSNormalizer {

    fun normalize(text: String): String {

        var normalized = text

        // Normalize currency variations
        normalized = normalized.replace(
            Regex("\\b(rs\\.?|inr\\.?)\\b", RegexOption.IGNORE_CASE),
            "INR"
        )

        // Normalize account references
        normalized = normalized.replace(
            Regex("\\b(a/c|acct|ac)\\b", RegexOption.IGNORE_CASE),
            "ACCOUNT"
        )

        // Normalize debit keywords
        normalized = normalized.replace(
            Regex("\\b(debited|debit)\\b", RegexOption.IGNORE_CASE),
            "DEBITED"
        )

        // Normalize credit keywords
        normalized = normalized.replace(
            Regex("\\b(credited|credit|received)\\b", RegexOption.IGNORE_CASE),
            "CREDITED"
        )

        // Normalize refund keywords
        normalized = normalized.replace(
            Regex("\\b(refunded|refund)\\b", RegexOption.IGNORE_CASE),
            "REFUND"
        )

        // Normalize transfer keywords
        normalized = normalized.replace(
            Regex("\\b(transferred|transfer)\\b", RegexOption.IGNORE_CASE),
            "TRANSFER"
        )

        // Remove multiple spaces
        normalized = normalized.replace(
            Regex("\\s+"),
            " "
        )

        return normalized.trim()
    }
}

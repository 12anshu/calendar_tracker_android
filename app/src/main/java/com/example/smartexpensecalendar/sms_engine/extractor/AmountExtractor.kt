package com.example.smartexpensecalendar.sms_engine.extractor

object AmountExtractor {

    private val amountPatterns = listOf(
        // Traditional prefixed: INR 500, Rs. 500, ₹500, Amt 500
        Regex(
            "(?:RS\\.?|INR|₹|AMT|AMOUNT)\\s*([0-9,]+(?:\\.[0-9]{1,2})?)",
            RegexOption.IGNORE_CASE
        ),
        // Suffix based: 500 RS, 500 INR
        Regex(
            "([0-9,]+(?:\\.[0-9]{1,2})?)\\s*(?:RS\\.?|INR)",
            RegexOption.IGNORE_CASE
        ),
        // Context based: debited for 500.00
        Regex(
            "(?:debited|credited|paid|spent)\\s+(?:for|of)\\s+([0-9,]+(?:\\.[0-9]{1,2})?)",
            RegexOption.IGNORE_CASE
        )
    )

    fun extractAmount(
        text: String
    ): Double? {

        amountPatterns.forEach { pattern ->

            val match =
                pattern.find(text)

            if (match != null) {

                return match
                    .groupValues[1]
                    .replace(",", "")
                    .toDoubleOrNull()
            }
        }

        return null
    }
}

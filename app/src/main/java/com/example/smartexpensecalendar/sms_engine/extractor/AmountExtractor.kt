package com.example.smartexpensecalendar.sms_engine.extractor

object AmountExtractor {

    private val amountPatterns = listOf(

        Regex(
            "(?:RS\\.?|INR|₹)\\s*([0-9,]+(?:\\.[0-9]{1,2})?)",
            RegexOption.IGNORE_CASE
        ),

        Regex(
            "([0-9,]+(?:\\.[0-9]{1,2})?)\\s*(?:RS\\.?|INR)",
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

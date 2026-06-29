package com.example.smartexpensecalendar.new_sms_engine.common.regex

/**
 * Regex patterns for extracting monetary amounts.
 *
 * NOTE:
 * These regexes are reused across Qualification,
 * Classification and Entity Intelligence.
 */
object AmountRegex {

    /**
     * Matches:
     * Rs 500
     * Rs.500
     * INR 500
     * ₹500
     * ₹ 1,250.50
     */
    val AMOUNT_REGEX = Regex(
        """(?i)(₹|RS\.?|INR)\s*[:.]?\s*(\d{1,3}(?:,\d{3})*(?:\.\d{1,2})?|\d+(?:\.\d{1,2})?)"""
    )

    /**
     * Matches amount without currency.
     *
     * Example:
     * amount 500
     * amt 1250
     */
    val AMOUNT_WITH_LABEL_REGEX = Regex(
        """(?i)(AMOUNT|AMT)\s*[:.]?\s*(\d{1,3}(?:,\d{3})*(?:\.\d{1,2})?|\d+(?:\.\d{1,2})?)"""
    )

    /**
     * Matches standalone numeric amount.
     *
     * Used as fallback only.
     */
    val STANDALONE_AMOUNT_REGEX = Regex(
        """\b\d{1,3}(?:,\d{3})*(?:\.\d{1,2})?\b"""
    )
}
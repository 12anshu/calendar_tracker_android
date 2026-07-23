package com.example.smartexpensecalendar.new_sms_engine.common.enums.amount

/**
 * Regex patterns for extracting monetary amounts.
 *
 * NOTE:
 * These regexes are reused across Qualification,
 * Classification and Entity Intelligence.
 */
object AmountRegex {

    private const val VALUE_PATTERN =
        """(?:\d[\d,]*)(?:\.\d{1,2})?"""

    val CURRENCY_PREFIX_REGEX = Regex(
        "(?i)(${AmountSignals.CURRENCY_PATTERN})\\s*[:.]?\\s*($VALUE_PATTERN)"
    )

    val CURRENCY_SUFFIX_REGEX = Regex(
        "(?i)($VALUE_PATTERN)\\s*(${AmountSignals.CURRENCY_PATTERN})"
    )

    val ATTACHED_CURRENCY_TOKEN_REGEX = Regex(
        """(?i)^(${AmountSignals.CURRENCY_PATTERN})\s*[:.]?\s*($VALUE_PATTERN)$"""
    )

}
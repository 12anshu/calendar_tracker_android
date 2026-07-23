package com.example.smartexpensecalendar.new_sms_engine.common.regex

/**
 * Regex patterns for SMS sender identifiers.
 */
object SenderRegex {

    /**
     * Standard Indian SMS Sender ID.
     *
     * Examples:
     * VM-HDFCBK-I
     * VK-ICICIB-R
     * JD-PAYTM-P
     * AX-SBIUPI-S
     */
    val STANDARD_SENDER_REGEX =
        Regex("^[A-Z]{2}-[A-Z0-9]{3,10}-[A-Z]$", RegexOption.IGNORE_CASE)

}
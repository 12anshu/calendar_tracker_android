package com.example.smartexpensecalendar.new_sms_engine.common.regex

/**
 * Regex patterns for SMS sender identifiers.
 */
object SenderRegex {

    /**
     * Standard Indian SMS Sender ID.
     *
     * Examples:
     * VM-HDFCBK
     * VK-ICICIB
     * JD-PAYTM
     * AX-SBIUPI
     */
    val STANDARD_SENDER_REGEX =
        Regex("^[A-Z]{2}-[A-Z0-9]{5,6}$")

    /**
     * Captures sender identifier.
     *
     * Example:
     * VM-HDFCBK -> HDFCBK
     */
    val SENDER_ID_REGEX =
        Regex("^[A-Z]{2}-([A-Z0-9]{5,6})$")
}
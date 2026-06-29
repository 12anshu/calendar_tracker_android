package com.example.smartexpensecalendar.new_sms_engine.entity.merchant.patterns

/**
 * Shared regular expressions used by Merchant Intelligence.
 */
object MerchantRegexPatterns {

    /**
     * UPI Handle
     */
    val UPI_HANDLE =
        Regex("""[A-Za-z0-9.\-_]{2,}@[A-Za-z]{2,}""")

    /**
     * UPI Transaction Id
     */
    val UPI_REFERENCE =
        Regex("""\b\d{10,20}\b""")
}
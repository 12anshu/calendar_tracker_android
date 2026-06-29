package com.example.smartexpensecalendar.new_sms_engine.common.regex

/**
 * Merchant related regular expressions.
 */
object MerchantRegex {

    val AFTER_PREPOSITION = Regex(
        """(?i)\b(?:at|to|from|via|by)\b\s+(.+)"""
    )
}
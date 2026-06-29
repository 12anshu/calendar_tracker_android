package com.example.smartexpensecalendar.new_sms_engine.common.regex

/**
 * Card related regular expressions.
 */
object CardRegex {

    val MASKED_CARD = Regex(
        """(?:card)\s*(?:ending|ending in)?\s*([Xx*0-9]{4,})"""
    )
}
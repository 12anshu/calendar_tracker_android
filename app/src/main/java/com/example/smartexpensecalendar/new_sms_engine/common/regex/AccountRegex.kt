package com.example.smartexpensecalendar.new_sms_engine.common.regex

/**
 * Account related regular expressions.
 */
object AccountRegex {

    val MASKED_ACCOUNT = Regex(
        """(?:a/c|account|acct)\s*(?:no\.?)?\s*[:\-]?\s*([Xx*0-9]{4,})"""
    )
}
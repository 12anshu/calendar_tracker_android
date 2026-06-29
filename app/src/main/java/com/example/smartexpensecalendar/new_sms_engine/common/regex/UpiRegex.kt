package com.example.smartexpensecalendar.new_sms_engine.common.regex

/**
 * UPI related regular expressions.
 */
object UpiRegex {

    val UPI_ID = Regex(
        """[A-Za-z0-9.\-_]{2,}@[A-Za-z]{2,}"""
    )

    val UPI_REFERENCE = Regex(
        """\b\d{10,20}\b"""
    )
}
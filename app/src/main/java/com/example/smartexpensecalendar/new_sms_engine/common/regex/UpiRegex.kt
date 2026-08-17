package com.example.smartexpensecalendar.new_sms_engine.common.regex

object UpiRegex {
    val UPI_PREFIX_REGEX =
        Regex("""(?i)^@?UPI[_:-]?""")

    val UPI_ID_REGEX =
        Regex("""^([A-Z0-9]+)(?:[._-].*?)?@[A-Z0-9]+$""", RegexOption.IGNORE_CASE)

}
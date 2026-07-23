package com.example.smartexpensecalendar.new_sms_engine.common.regex

object UpiPrefixRegex {
    val UPI_PREFIX_REGEX =
        Regex("""(?i)^@?UPI[_:-]?""")
}
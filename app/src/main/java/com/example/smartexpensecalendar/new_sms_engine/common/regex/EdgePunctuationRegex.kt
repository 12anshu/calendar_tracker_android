package com.example.smartexpensecalendar.new_sms_engine.common.regex

object EdgePunctuationRegex{

    val EDGE_PUNCTUATION_REGEX =
        Regex("""^[^\p{L}\p{N}]+|[^\p{L}\p{N}]+$""")

}
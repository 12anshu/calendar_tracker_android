package com.example.smartexpensecalendar.sms.utils

object KeywordMatcher {

    fun containsKeyword(
        text: String,
        keyword: String
    ): Boolean {
        return Regex(
            "\\b${Regex.escape(keyword)}\\b"
        ).containsMatchIn(text)
    }
}

package com.example.smartexpensecalendar.new_sms_engine.common.utils

object RegexUtils {

    fun keywordPattern(keywords: Set<String>): String =
        keywords
            .map(Regex::escape)
            .sortedByDescending { it.length }
            .joinToString("|")
}
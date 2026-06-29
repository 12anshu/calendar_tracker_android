package com.example.smartexpensecalendar.new_sms_engine.common.utils

/**
 * Utility for matching signals against SMS text.
 */
object MatchUtils {

    fun findMatches(
        message: String,
        signals: Set<String>
    ): List<String> {

        val normalizedMessage = message.uppercase()

        return signals.filter {
            normalizedMessage.contains(it)
        }
    }

    fun normalize(
        message: String
    ): String {

        return message
            .uppercase()
            .trim()
            .replace(Regex("\\s+"), " ")
    }
}
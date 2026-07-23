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

    fun findSignalMatches(
        message: String,
        signals: Set<String>
    ): List<String> {

        val normalizedMessage = normalize(message)

        return signals.filter {
            normalizedMessage.contains(it.uppercase())
        }
    }

    /**
     * Checks if any of the provided patterns exist in the message.
     */
    fun containsAny(
        message: String,
        patterns: Set<String>
    ): Boolean {

        val normalizedMessage = normalize(message)

        return patterns.any {
            normalizedMessage.contains(it.uppercase())
        }
    }

    /**
     * Finds all patterns that exist in the message.
     */
    fun findPatternMatches(
        message: String,
        patterns: Set<String>
    ): List<String> {

        val normalizedMessage = normalize(message)

        return patterns.filter {
            normalizedMessage.contains(it.uppercase())
        }
    }

}
package com.example.smartexpensecalendar.sms

object SMSNormalizer {

    /**
     * Normalizes whitespace and common symbols without altering the core banking verbs.
     * This ensures our phrase-based detection matches the actual SMS templates.
     */
    fun normalize(text: String): String {
        if (text.isBlank()) return ""

        var normalized = text.uppercase()

        // 1. Normalize currency variations to a standard anchor for easier regex
        normalized = normalized.replace(Regex("\\b(RS\\.?|INR|₹|RE\\.?)\\b"), "INR")

        // 2. Remove multiple spaces and newlines
        normalized = normalized.replace(Regex("\\s+"), " ")

        return normalized.trim()
    }
}

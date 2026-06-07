package com.example.smartexpensecalendar.sms.utils

object PhraseMatcher {

    fun countMatches(
        text: String,
        phrases: Set<String>
    ): Int {

        val normalized = text.uppercase()

        return phrases.count {
            normalized.contains(it.uppercase())
        }
    }

    fun findMatches(
        text: String,
        phrases: Set<String>
    ): Set<String> {

        val normalized = text.uppercase()

        return phrases.filter {
            normalized.contains(it.uppercase())
        }.toSet()
    }

    fun matchedPhrases(
        text: String,
        phrases: Set<String>
    ): Set<String> {

        val normalized = text.uppercase()

        return phrases.filter {
            normalized.contains(it.uppercase())
        }.toSet()
    }
}

package com.example.smartexpensecalendar.new_sms_engine.common.segmentation.rules

import com.example.smartexpensecalendar.new_sms_engine.common.segmentation.BoundaryContext

class SentenceBoundaryRule : SegmentBoundaryRule {

    override fun matches(
        context: BoundaryContext
    ): Boolean {

        val current = context.current.text.trim()

        if (!current.endsWith(".")) {
            return false
        }

        if (isDecimal(current)) {
            return false
        }

        if (isKnownAbbreviation(current)) {
            return false
        }

        val next = context.next ?: return true

        return next.lineIndex != context.current.lineIndex ||
                startsNewSentence(next.text)
    }

    private fun isDecimal(
        text: String
    ): Boolean {

        return Regex("\\d+\\.\\d+").matches(text)
    }

    private fun isKnownAbbreviation(
        text: String
    ): Boolean {

        val normalized = text
            .removeSuffix(".")
            .uppercase()

        return normalized in ABBREVIATIONS
    }

    private fun startsNewSentence(
        text: String
    ): Boolean {

        val first = text.firstOrNull() ?: return false

        return first.isUpperCase()
    }

    companion object {

        private val ABBREVIATIONS = setOf(
            "RS",
            "NO",
            "REF",
            "TXN",
            "AC",
            "A/C",
            "DR",
            "MR",
            "MRS",
            "MS",
            "PROF",
            "LTD",
            "PVT",
            "CO"
        )
    }
}
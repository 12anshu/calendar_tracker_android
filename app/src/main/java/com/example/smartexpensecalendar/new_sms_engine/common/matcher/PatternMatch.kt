package com.example.smartexpensecalendar.new_sms_engine.common.matcher

/**
 * Represents a successful pattern match.
 */
data class PatternMatch(

    val patternName: String,

    /**
     * Token indices that participated in the match.
     */
    val matchedIndices: List<Int>

) {

    val startIndex: Int
        get() = matchedIndices.first()

    val endIndex: Int
        get() = matchedIndices.last()
}
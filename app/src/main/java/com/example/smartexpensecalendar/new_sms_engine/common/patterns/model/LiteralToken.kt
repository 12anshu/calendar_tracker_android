package com.example.smartexpensecalendar.new_sms_engine.common.patterns.model

import com.example.smartexpensecalendar.new_sms_engine.classification.patterns.model.PatternToken

/**
 * Matches an exact token value.
 *
 * Example:
 *  "PAYMENT"
 *  "MINIMUM"
 *  "DUE"
 */
data class LiteralToken(
    val values: Set<String>,
    val ignoreCase: Boolean = true
) : PatternToken {

    constructor(
        vararg values: String
    ) : this(values.toSet())
}
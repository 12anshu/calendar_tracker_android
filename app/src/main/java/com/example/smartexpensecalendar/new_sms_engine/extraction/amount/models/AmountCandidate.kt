package com.example.smartexpensecalendar.new_sms_engine.extraction.amount.models

/**
 * Represents a possible transaction amount extracted
 * from the SMS.
 *
 * A message may contain multiple candidates.
 */
data class AmountCandidate(

    /**
     * Parsed monetary value.
     */
    val money: Money,

    /**
     * Inclusive start character index.
     */
    val startIndex: Int,

    /**
     * Inclusive end character index.
     */
    val endIndex: Int,

    /**
     * Surrounding message context used by the resolver.
     */
    val context: String,

    /**
     * Exact text matched by the regex.
     */
    val rawMatch: String
)
package com.example.smartexpensecalendar.new_sms_engine.qualification.models

/**
 * Result produced by Sender Qualification.
 */
data class SenderQualificationResult(

    /**
     * Whether the sender is recognised as a financial sender.
     */
    val qualified: Boolean,

    /**
     * Confidence score (0-100).
     */
    val confidence: Int,

    /**
     * Qualification score.
     */
    val score: Int,

    /**
     * Qualified sender identifier.
     */
    val sender: String,

    /**
     * Evidence collected during qualification.
     */
    val evidence: List<String> = emptyList(),

    /**
     * Rules executed during qualification.
     */
    val executedRules: List<String> = emptyList()
)

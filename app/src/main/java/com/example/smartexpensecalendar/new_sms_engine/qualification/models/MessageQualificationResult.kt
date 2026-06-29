package com.example.smartexpensecalendar.new_sms_engine.qualification.models

/**
 * Result produced by Message Qualification.
 */
data class MessageQualificationResult(

    /**
     * Whether the message appears financial.
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
     * Evidence collected during qualification.
     */
    val evidence: List<String> = emptyList(),

    /**
     * Rules executed during qualification.
     */
    val executedRules: List<String> = emptyList()
)

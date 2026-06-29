package com.example.smartexpensecalendar.new_sms_engine.qualification.models

/**
 * Final Qualification result.
 */
data class QualificationResult(

    /**
     * Final qualification decision.
     */
    val qualified: Boolean,

    /**
     * Overall confidence.
     */
    val confidence: Int,

    /**
     * Overall score.
     */
    val score: Int,

    /**
     * Sender qualification.
     */
    val sender: SenderQualificationResult,

    /**
     * Message qualification.
     */
    val message: MessageQualificationResult
)
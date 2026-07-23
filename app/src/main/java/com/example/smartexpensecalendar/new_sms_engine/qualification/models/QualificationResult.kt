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
     * Sender qualification.
     */
    val sender: String,

)
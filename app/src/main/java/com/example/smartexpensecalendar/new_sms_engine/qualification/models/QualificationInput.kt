package com.example.smartexpensecalendar.new_sms_engine.qualification.models

/**
 * Input to the Qualification phase.
 */
data class QualificationInput(

    /**
     * SMS sender.
     */
    val sender: String,

    /**
     * SMS body.
     */
    val message: String
)
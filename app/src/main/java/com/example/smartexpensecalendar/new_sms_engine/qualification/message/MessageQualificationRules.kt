package com.example.smartexpensecalendar.new_sms_engine.qualification.message

/**
 * Evaluates financial evidence from an SMS.
 */
interface MessageQualificationRules {

    /**
     * Returns all matched evidence.
     */
    fun evaluate(message: String): List<String>
}
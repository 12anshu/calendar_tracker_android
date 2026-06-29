package com.example.smartexpensecalendar.new_sms_engine.qualification.sender.evaluators

/**
 * Evaluates whether a sender exists in the financial sender registry.
 */
interface SenderRegistryEvaluator {

    /**
     * Returns evidence collected from registry lookup.
     */
    fun evaluate(sender: String): List<String>
}
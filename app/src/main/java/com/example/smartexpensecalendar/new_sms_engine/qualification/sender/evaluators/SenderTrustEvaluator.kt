package com.example.smartexpensecalendar.new_sms_engine.qualification.sender.evaluators

/**
 * Evaluates sender trustworthiness.
 */
interface SenderTrustEvaluator {

    /**
     * Returns evidence collected from trust analysis.
     */
    fun evaluate(sender: String): List<String>
}
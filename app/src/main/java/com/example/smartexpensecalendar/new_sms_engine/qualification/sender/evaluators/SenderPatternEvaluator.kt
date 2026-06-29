package com.example.smartexpensecalendar.new_sms_engine.qualification.sender.evaluators

/**
 * Evaluates sender naming conventions.
 *
 * Example:
 * VM-HDFCBK
 * AX-SBIUPI
 * JD-PAYTM
 */
interface SenderPatternEvaluator {

    /**
     * Returns evidence collected from sender pattern analysis.
     */
    fun evaluate(sender: String): List<String>
}
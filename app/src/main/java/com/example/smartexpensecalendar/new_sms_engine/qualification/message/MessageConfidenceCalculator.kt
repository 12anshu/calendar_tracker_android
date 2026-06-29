package com.example.smartexpensecalendar.new_sms_engine.qualification.message

/**
 * Calculates confidence from evaluation score.
 */
class MessageConfidenceCalculator {

    fun calculate(
        score: Int
    ): Int {

        return score.coerceIn(0, 100)
    }
}
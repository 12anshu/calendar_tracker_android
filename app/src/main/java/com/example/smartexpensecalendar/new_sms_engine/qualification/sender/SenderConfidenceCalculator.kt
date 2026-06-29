package com.example.smartexpensecalendar.new_sms_engine.qualification.sender

/**
 * Calculates sender qualification confidence.
 */
class SenderConfidenceCalculator {

    fun calculate(
        evidence: List<String>
    ): Int {

        var score = 0

        evidence.forEach { signal ->

            when (signal) {

                "VALID_SENDER_FORMAT" -> score += 100
            }
        }

        return score.coerceIn(0, 100)
    }
}
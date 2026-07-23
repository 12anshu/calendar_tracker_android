package com.example.smartexpensecalendar.new_sms_engine.common.confidence

object ConfidenceCalculator {

    /**
     * Returns confidence between 0.0 and 1.0
     * based on winner vs competing score.
     */
    fun fromScores(
        winner: Int,
        competitor: Int
    ): Float {

        val total = winner + competitor

        if (total <= 0) return 0f

        return (winner.toFloat() / total)
            .coerceIn(0f, 1f)
    }
}
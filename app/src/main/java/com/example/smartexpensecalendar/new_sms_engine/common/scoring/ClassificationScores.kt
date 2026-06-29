package com.example.smartexpensecalendar.new_sms_engine.common.scoring

/**
 * Scores used during Classification.
 */
object ClassificationScores {

    /**
     * Strong evidence.
     */
    const val STRONG_MATCH = 80

    /**
     * Medium evidence.
     */
    const val MEDIUM_MATCH = 60

    /**
     * Weak evidence.
     */
    const val WEAK_MATCH = 40

    /**
     * Default confidence.
     */
    const val UNKNOWN = 0
}
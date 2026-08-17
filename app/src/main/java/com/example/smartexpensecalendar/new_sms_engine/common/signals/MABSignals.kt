package com.example.smartexpensecalendar.new_sms_engine.common.signals

/**
 * Vocabulary representing average-balance requirements used by banks.
 *
 * These terms describe a balance-maintenance requirement,
 * not an actual account balance or money movement.
 */
object MABSignals {

    /**
     * Explicit banking abbreviations.
     */
    val EXPLICIT_SIGNALS = setOf(
        "MAB",
        "AMB",
        "AQB",
        "QAB"
    )

    /**
     * Descriptive phrases for balance requirements.
     */
    val DESCRIPTIVE_SIGNALS = setOf(
        "monthly average balance",
        "average monthly balance",
        "minimum monthly balance",
        "average monthly minimum balance",

        "minimum average balance",
        "average minimum balance",

        "quarterly average balance",
        "average quarterly balance",
        "quarterly minimum balance",
        "average quarterly minimum balance"
    )

    /**
     * All signals related to average balance requirements.
     */
    val ALL = setOf(
        EXPLICIT_SIGNALS,
        DESCRIPTIVE_SIGNALS
    ).flatten().toSet()
}
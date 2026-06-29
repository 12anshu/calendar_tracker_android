package com.example.smartexpensecalendar.new_sms_engine.common.signals

/**
 * Common language signals shared across the SMS Engine.
 *
 * These signals are generic and are not owned by any
 * specific business domain.
 */
object CommonSignals {

    /**
     * Common prepositions.
     */
    val PREPOSITION_SIGNALS = setOf(
        "AT",
        "TO",
        "FROM",
        "BY",
        "VIA",
        "ON",
        "FOR",
        "IN",
        "INTO",
        "USING",
        "THROUGH",
        "WITH",
        "TOWARDS"
    )

    /**
     * Direction anchors.
     */
    val DIRECTION_ANCHOR_SIGNALS = setOf(
        "AT",
        "TO",
        "FROM",
        "TOWARDS"
    )

    /**
     * Generic connector words.
     */
    val CONNECTOR_SIGNALS = setOf(
        "AND",
        "OR",
        "OF",
        "THE",
        "YOUR",
        "OUR"
    )

    /**
     * Generic transaction identifiers.
     */
    val TRANSACTION_IDENTIFIER_SIGNALS = setOf(
        "TXN",
        "TRANSACTION",
        "REFERENCE",
        "REF",
        "UTR",
        "RRN"
    )

    /**
     * Currency indicators.
     */
    val CURRENCY_SIGNALS = setOf(
        "₹",
        "RS",
        "INR"
    )

    /**
     * Generic amount indicators.
     */
    val AMOUNT_SIGNALS = setOf(
        "AMOUNT",
        "AMT"
    )

    /**
     * Generic date/time indicators.
     */
    val DATE_TIME_SIGNALS = setOf(
        "DATE",
        "TIME",
        "TODAY",
        "YESTERDAY"
    )
}
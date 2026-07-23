package com.example.smartexpensecalendar.new_sms_engine.common.signals

/**
 * Common language signals shared across the SMS Engine.
 *
 * These signals are generic and are not owned by any
 * specific business domain.
 */
object CommonSignals {

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
}
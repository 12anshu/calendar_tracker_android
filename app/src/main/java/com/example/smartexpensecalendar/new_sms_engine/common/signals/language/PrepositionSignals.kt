package com.example.smartexpensecalendar.new_sms_engine.common.signals.language

object PrepositionSignals {

    val DESTINATION = setOf(
        "TO",
        "TOWARDS",
        "INTO"
    )

    val SOURCE = setOf(
        "FROM"
    )

    val LOCATION = setOf(
        "AT"
    )

    val MEDIUM = setOf(
        "BY",
        "THROUGH",
        "USING"
    )

    val PURPOSE = setOf(
        "FOR"
    )

    val REFERENCE = setOf(
        "AGAINST"
    )

    val ASSOCIATION = setOf(
        "ON"
    )

    val ALL = setOf(DESTINATION,
            SOURCE,
            LOCATION,
            MEDIUM,
            PURPOSE,
            REFERENCE,
            ASSOCIATION
    ).flatten().toSet()
}
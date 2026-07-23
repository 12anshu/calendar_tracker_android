package com.example.smartexpensecalendar.new_sms_engine.common.signals.language

object AuxiliarySignals {

    /**
     * Present auxiliary verbs.
     */
    val PRESENT = setOf(
        "IS",
        "AM",
        "ARE",
        "DO",
        "DOES",
        "HAS",
        "HAVE"
    )

    /**
     * Past auxiliary verbs.
     */
    val PAST = setOf(
        "WAS",
        "WERE",
        "HAD",
        "DID"
    )

    /**
     * Future auxiliary verbs.
     */
    val FUTURE = setOf(
        "WILL",
        "SHALL"
    )

    /**
     * Progressive / passive helpers.
     */
    val PARTICIPLES = setOf(
        "BE",
        "BEEN",
        "BEING"
    )
}
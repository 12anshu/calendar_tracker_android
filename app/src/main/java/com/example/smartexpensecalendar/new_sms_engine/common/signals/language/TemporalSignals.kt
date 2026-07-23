package com.example.smartexpensecalendar.new_sms_engine.common.signals.language

object TemporalSignals {

    /**
     * Relative time expressions.
     */
    val RELATIVE = setOf(

        "TODAY",
        "TOMORROW",
        "YESTERDAY",

        "NOW",
        "CURRENTLY",

        "SOON",
        "LATER",

        "NEXT",
        "LAST",

        "UPCOMING"
    )

    /**
     * Scheduling expressions.
     */
    val SCHEDULING = setOf(

        "MINIMUM DUE",

        "TOTAL DUE",

        "AMOUNT DUE",

        "PAY BEFORE",

        "PAY BY",

        "DUE DATE",

        "EMI DUE",

        "PAY IMMEDIATELY",

        "PAY NOW",

        "AUTOPAY SCHEDULED",

        "AUTOPAY DUE"
    )
}
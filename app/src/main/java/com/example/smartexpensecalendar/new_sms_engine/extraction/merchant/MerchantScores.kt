package com.example.smartexpensecalendar.new_sms_engine.extraction.merchant

object MerchantScores {

    // Anchor based

    const val AFTER_ANCHOR = 100

    const val TO_ANCHOR = 50

    // Structured line

    const val LINE = 50

    const val UPPERCASE_LINE = 10

    const val TITLE_CASE_LINE = 5

    // Penalties

    const val HAS_AMOUNT = -100

    const val HAS_DATE = -100

    const val HAS_ACCOUNT = -80

    const val HAS_BANK = -80

    const val HAS_BALANCE = -80

    const val TOO_SHORT = -100

    const val ONLY_NUMBERS = -100
}
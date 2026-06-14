package com.example.smartexpensecalendar.sms.config

object DetectionConstants {

    const val FINANCIAL_THRESHOLD = 50

    const val STRONG_SIGNAL_SCORE = 30

    const val MEDIUM_SIGNAL_SCORE = 15

    const val AMOUNT_PATTERN_SCORE = 25

    const val NEGATIVE_SIGNAL_SCORE = -40

    const val ACCOUNT_PATTERN_SCORE = 20

    const val CARD_PATTERN_SCORE = 20

    const val UPI_PATTERN_SCORE = 30

    const val BALANCE_PATTERN_SCORE = 15

    const val MULTIPLE_SIGNAL_BONUS = 10

    const val MESSAGE_TYPE_KEYWORD_SCORE = 5

    const val MESSAGE_TYPE_PHRASE_SCORE = 10

    const val STRONG_OBLIGATION_SCORE = 50

    const val STRONG_INFORMATION_SCORE = 15

    const val STRONG_PROMOTIONAL_SCORE = 50

    const val MESSAGE_TYPE_MIN_THRESHOLD = 20

    // --- SHARED REGEX FRAGMENTS ---
    // Covers: Rs, Rs., INR, ₹, $, Amt, Amount, Re, Re.
    const val CURRENCY_SYMBOLS = "(?:RS\\.?|INR|₹|AMT|AMOUNT|RE\\.?|\\$)"

    // --- CONTEXTUAL PENALTIES (Merged V2 Intelligence) ---
    const val FAILED_TXN_PENALTY = -100

    const val REPORTING_CONTEXT_PENALTY = -100

    const val NO_ANCHOR_PENALTY = -80
}

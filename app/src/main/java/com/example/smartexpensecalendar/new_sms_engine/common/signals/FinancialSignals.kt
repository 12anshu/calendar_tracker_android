package com.example.smartexpensecalendar.new_sms_engine.common.signals

/**
 * Financial domain specific vocabulary.
 *
 * These are single-word business terms.
 */
object FinancialSignals {

    /**
     * Indicates payment obligations.
     */
    val OBLIGATION_SIGNALS = setOf(

        "EMI",
        "INSTALLMENT",
        "AUTOPAY",
        "MANDATE",
        "OUTSTANDING",
        "OVERDUE",
        "DUES",
        "LIABILITY"
    )

    /**
     * Indicates informational financial messages.
     */
    val INFORMATION_SIGNALS = setOf(

        "BALANCE",
        "STATEMENT",
        "SUMMARY",
        "LIMIT",
        "REWARD",
        "POINTS",
        "CASHBACK",
        "KYC",
        "PROFILE",
        "ACCOUNT"
    )

    val DUE_CONTEXT = setOf(

        "DUE",

        "PAYABLE",

        "OVERDUE"
    )
}
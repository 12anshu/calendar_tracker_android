package com.example.smartexpensecalendar.new_sms_engine.common.signals

object InformationSignals {

    /**
     * Statements / documents.
     */
    val STATEMENT_SIGNALS = setOf(

        "STATEMENT",
        "ESTATEMENT",
        "E-STATEMENT",
        "SUMMARY"
    )

    /**
     * Account lifecycle / maintenance.
     */
    val ACCOUNT_UPDATE_SIGNALS = setOf(

        "KYC",
        "REGISTERED",
        "UPDATED",
        "ACTIVATED",
        "DEACTIVATED",
        "BLOCKED",
        "UNBLOCKED",
        "LINKED"
    )

    /**
     * Security notifications.
     */
    val SECURITY_SIGNALS = setOf(

        "SECURITY",
        "ALERT",
        "FRAUD",
        "SAFE",
        "PROTECT"
    )

    /**
     * Rewards / loyalty.
     */
    val REWARD_SIGNALS = setOf(

        "REWARD",
        "REWARDS",
        "POINTS",
        "MILES",
        "NEUCOINS",
        "CASHBACK"
    )
}
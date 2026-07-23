package com.example.smartexpensecalendar.new_sms_engine.common.signals

object TransactionNegativeSignals {

    /**
     * Transaction was not completed.
     */
    val FAILURE_SIGNALS = setOf(
        "FAILED",
        "FAILURE",
        "DECLINED",
        "DECLINE",
        "REJECTED",
        "REJECT",
        "UNSUCCESSFUL",
        "CANCELLED",
        "CANCELED"
    )

    /**
     * Authentication request.
     */
    val AUTHENTICATION_SIGNALS = setOf(
        "OTP",
        "ONE-TIME",
        "ONETIME"
    )

    /**
     * User approval required.
     */
    val AUTHORIZATION_SIGNALS = setOf(
        "AUTHORISE",
        "AUTHORIZE",
        "APPROVE",
        "CONSENT",
        "CONFIRM"
    )
}
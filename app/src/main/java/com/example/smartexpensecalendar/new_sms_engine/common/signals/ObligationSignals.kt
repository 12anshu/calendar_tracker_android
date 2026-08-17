package com.example.smartexpensecalendar.new_sms_engine.common.signals

object ObligationSignals {

    /**
     * Financial liabilities.
     */
    val LIABILITY_OBJECT_SIGNALS = setOf(

        "BILL",
        "EMI",
        "EMIS",
        "INSTALLMENT",
        "INSTALMENT",

        "PREMIUM",

        "DUES",

        "CHARGES",

        "FEE",
        "FEES",
        "REPAYMENT"
    )

    /**
     * Liability state.
     */
    val LIABILITY_STATE_SIGNALS = setOf(

        "DUE",

        "OUTSTANDING",

        "OVERDUE",

        "PAYABLE",

        "PENDING",

        "UNPAID"
    )

    /**
     * Customer action.
     */
    val REQUEST_ACTION_SIGNALS = setOf(

        "PAY",

        "CLEAR",

        "MAINTAIN",

        "RECHARGE",

        "RENEW",

        "SETTLE"
    )
}
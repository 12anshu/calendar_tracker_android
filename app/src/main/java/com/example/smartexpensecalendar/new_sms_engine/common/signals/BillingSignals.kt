package com.example.smartexpensecalendar.new_sms_engine.common.signals

object BillingSignals {

    val BILLING_OBJECTS = setOf(

        "PAYMENT",

        "BILL",

        "EMI",

        "INSTALLMENT",

        "STATEMENT",

        "LOAN",

        "RECHARGE",

        "SUBSCRIPTION",

        "PREMIUM",

        "RENEWAL",

        "AMOUNT"
    )

    val DUE_KEYWORDS = setOf(

        "DUE",

        "DUES",

        "OVERDUE",

        "PAYABLE"
    )

    val AMOUNT_DESCRIPTORS = setOf(

        "MINIMUM",

        "TOTAL",

        "CURRENT",

        "OUTSTANDING"
    )

    val ALL = BILLING_OBJECTS + DUE_KEYWORDS + AMOUNT_DESCRIPTORS
}
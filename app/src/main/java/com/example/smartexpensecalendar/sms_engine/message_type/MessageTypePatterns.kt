package com.example.smartexpensecalendar.sms_engine.message_type

object MessageTypePatterns {

    val PHRASES_OBLIGATION = listOf(
        "DUE ON", "DUE BY", "PAY BY", "PAY BEFORE", "MINIMUM AMOUNT", "MIN AMT",
        "STATEMENT FOR", "STMT GEN", "BILL OF", "BILL GENERATED", "UPCOMING DEBIT",
        "WILL BE DEBITED", "WILL BE CHARGED", "RECHARGE DUE"
    )

    val PHRASES_INFORMATION = listOf(
        "BAL IS", "BALANCE IS", "AVL BAL", "LIMIT IS", "STMT GEN", "TOTAL SPENT",
        "WAS DEBITED ON", "WAS CREDITED ON"
    )

    val COMPLETED_ACTION_SIGNALS = listOf(
        "DEBITED",
        "CREDITED",
        "PAID",
        "RECEIVED",
        "SPENT",
        "PURCHASED",
        "TRANSFERRED",
        "WITHDRAWN",
        "REFUNDED"
    )

    val SUCCESS_SIGNALS = listOf(
        "SUCCESSFUL",
        "SUCCESS",
        "COMPLETED",
        "PROCESSED"
    )

    val TRANSACTION_SIGNALS = listOf(
        "TXN",
        "TRANSACTION",
        "TXN ID",
        "UTR",
        "RRN"
    )

    val FUTURE_SIGNALS = listOf(
        "DUE",
        "PAY BY",
        "PAY BEFORE",
        "AMOUNT DUE",
        "OUTSTANDING",
        "MINIMUM DUE",
        "UPCOMING",
        "SCHEDULED",
        "MINIMUM DUE",
        "PAY NOW",
        "DUE DATE",
        "OUTSTANDING AMOUNT",
        "AMOUNT DUE",
        "BILL DUE",
        "RECHARGE DUE",
        "PAYMENT REQUESTED",
        "REQUESTED A PAYMENT"
    )

    val REPORTING_SIGNALS = listOf(
        "BALANCE",
        "LIMIT",
        "STATEMENT",
        "REWARD",
        "VOUCHER",
        "ACCOUNT ACTIVE",
        "ACCOUNT ACTIVATED",
        "EMI CONVERSION",
        "CONVERTED INTO EMI",
        "ACCOUNT IS NOW ACTIVE",
        "CARD ACTIVATED",
        "CARD DISPATCHED",
        "LIMIT ENHANCEMENT",
        "LIMIT INCREASE",
        "LOUNGE",
        "REWARD POINTS",
        "QUALIFIED FOR",
        "REVISED",
        "CHARGES",
        "MAINTENANCE CHARGES",
        "ANNUAL MAINTENANCE"
    )

    val STRONG_TRANSACTION_SIGNALS = listOf(
        "TXN",
        "TRANSACTION",
        "TXN ID",
        "UTR",
        "RRN"
    )

    val STATUS_SIGNALS = listOf(
        "SUCCESSFUL",
        "SUCCESS",
        "COMPLETED",
        "PROCESSED"
    )
}
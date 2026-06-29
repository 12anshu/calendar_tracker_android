package com.example.smartexpensecalendar.new_sms_engine.common.patterns

/**
 * Reusable message type language patterns.
 *
 * These patterns classify an SMS as:
 * Transaction, Obligation or Information.
 */
object MessageTypePatterns {

    /**
     * Future obligation patterns.
     */
    val OBLIGATION_PATTERNS = setOf(

        "DUE ON",
        "DUE BY",

        "PAY BY",
        "PAY BEFORE",
        "PAY NOW",

        "MINIMUM AMOUNT",
        "MINIMUM DUE",
        "MIN AMT",

        "AMOUNT DUE",
        "OUTSTANDING",
        "OUTSTANDING AMOUNT",

        "BILL OF",
        "BILL DUE",
        "BILL GENERATED",

        "STATEMENT FOR",

        "UPCOMING DEBIT",
        "WILL BE DEBITED",
        "WILL BE CHARGED",

        "PAYMENT REQUESTED",
        "REQUESTED A PAYMENT",

        "RECHARGE DUE"
    )

    /**
     * Informational patterns.
     */
    val INFORMATION_PATTERNS = setOf(

        "BAL IS",
        "BALANCE IS",

        "AVL BAL",

        "LIMIT IS",
        "LIMIT ENHANCEMENT",
        "LIMIT INCREASE",

        "STMT GEN",
        "STATEMENT",

        "TOTAL SPENT",

        "REWARD POINTS",
        "VOUCHER",

        "ACCOUNT ACTIVE",
        "ACCOUNT ACTIVATED",
        "ACCOUNT IS NOW ACTIVE",

        "CARD ACTIVATED",
        "CARD DISPATCHED",

        "EMI CONVERSION",
        "CONVERTED INTO EMI",

        "QUALIFIED FOR",

        "REVISED",

        "CHARGES",
        "MAINTENANCE CHARGES",
        "ANNUAL MAINTENANCE"
    )

    /**
     * Strong transaction patterns.
     */
    val TRANSACTION_PATTERNS = setOf(

        "TXN",

        "TRANSACTION",

        "TXN ID",

        "UTR",

        "RRN"
    )

    /**
     * Completed transaction patterns.
     */
    val COMPLETED_TRANSACTION_PATTERNS = setOf(

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

    /**
     * Success status patterns.
     */
    val SUCCESS_PATTERNS = setOf(

        "SUCCESS",

        "SUCCESSFUL",

        "COMPLETED",

        "PROCESSED"
    )

    /**
     * Future tense patterns.
     */
    val FUTURE_PATTERNS = setOf(

        "DUE",

        "UPCOMING",

        "SCHEDULED",

        "WILL",

        "BEFORE"
    )

    /**
     * Reporting patterns.
     */
    val REPORTING_PATTERNS = setOf(

        "BALANCE",

        "LIMIT",

        "STATEMENT",

        "REWARD",

        "VOUCHER",

        "CHARGES"
    )
}
package com.example.smartexpensecalendar.new_sms_engine.common.patterns

/**
 * Reusable direction language patterns.
 *
 * These patterns are stronger than individual signals and
 * are primarily used by Direction Classification.
 */
object DirectionPatterns {

    /**
     * Strong debit patterns.
     */
    val DEBIT_PATTERNS = setOf(

        "DEBITED FROM",
        "DEBITED TOWARDS",

        "SPENT ON",
        "SPENT AT",
        "SPENT FROM",
        "SPENT USING",
        "SPENT INR",
        "SPENT RS",

        "PURCHASE AT",
        "PURCHASE OF",
        "PURCHASE FOR",
        "PURCHASE MADE",
        "PURCHASE USING",
        "PURCHASE ON",

        "PAID TO",
        "PAID VIA",
        "PAID USING",
        "PAYMENT OF",

        "ATM WITHDRAWAL",
        "CASH WITHDRAWAL",

        "WITHDRAWN FROM",
        "WITHDRAWN AT",

        "SENT TO",
        "SENT FROM",
        "SENT FROM YOUR",

        "TRANSFERRED FROM YOUR",

        "AUTOPAY SUCCESSFUL",
        "E-MANDATE SUCCESS",

        "MERCHANT PAYMENT",
        "UPI PAYMENT",
        "DEBIT TRANSACTION"
    )

    /**
     * Strong credit patterns.
     */
    val CREDIT_PATTERNS = setOf(

        "PAYMENT RECEIVED",
        "RECEIVED TOWARDS",
        "RECEIVED FROM",
        "RECEIVED ON",
        "RECEIVED VIA UPI",

        "CREDITED TO",
        "CREDITED INTO",
        "CREDITED IN",
        "CREDITED WITH",
        "CREDITED SUCCESSFULLY",

        "AMOUNT CREDITED",
        "AMT CREDITED",

        "MONEY CREDITED",
        "MONEY RECEIVED",

        "FUNDS RECEIVED",

        "DEPOSITED INTO",
        "DEPOSITED TO",
        "DEPOSITED IN",
        "DEPOSITED TOWARDS",

        "SUCCESSFULLY CREDITED",

        "SALARY DEPOSITED",

        "INTEREST CREDITED",

        "REWARD CREDITED",

        "CASHBACK CREDITED",

        "REFUND CREDITED",

        "REVERSAL OF",

        "INWARD REMITTANCE",

        "LOADED IN WALLET",
        "ADDED IN WALLET"
    )

    /**
     * Override patterns.
     * These have higher priority than normal debit/credit signals.
     */
    val CREDIT_OVERRIDE_PATTERNS = setOf(
        "REFUND",
        "REVERSAL",
        "CASHBACK",
        "INTEREST CREDITED",
        "SALARY"
    )

    val DEBIT_OVERRIDE_PATTERNS = setOf(
        "DEBITED",
        "SPENT",
        "PAID",
        "PURCHASE",
        "WITHDRAWAL",
        "DEDUCTED"
    )

    /**
     * Direction anchors.
     */
    val DEBIT_ANCHOR_PATTERNS = setOf(
        "AT",
        "TO",
        "TOWARDS"
    )

    val CREDIT_ANCHOR_PATTERNS = setOf(
        "FROM"
    )
}
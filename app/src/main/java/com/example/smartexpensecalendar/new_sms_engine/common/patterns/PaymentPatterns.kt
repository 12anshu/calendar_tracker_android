package com.example.smartexpensecalendar.new_sms_engine.common.patterns

/**
 * Reusable payment related language patterns.
 *
 * These are semantic phrases rather than individual signals.
 */
object PaymentPatterns {

    /**
     * Debit transaction patterns.
     */
    val DEBIT_PATTERNS = setOf(

        "DEBITED FROM",
        "DEBITED TOWARDS",
        "AMOUNT DEBITED",
        "AMOUNT DEDUCTED",
        "DEDUCTED FROM",
        "AMT DEDUCTED",

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
        "PAID INR",
        "PAID RS",

        "PAYMENT OF",

        "CASH WITHDRAWAL",
        "ATM WITHDRAWAL",

        "WITHDRAWN FROM",
        "WITHDRAWN AT",

        "SENT TO",
        "SENT FROM",
        "SENT FROM YOUR",

        "MONEY SENT",
        "MONEY DEBITED",

        "CHARGED TO",

        "AUTOPAY SUCCESSFUL",
        "E-MANDATE SUCCESS",

        "BILL PAYMENT",
        "MERCHANT PAYMENT",

        "DEBIT TRANSACTION",
        "UPI PAYMENT",

        "TRANSFERRED FROM YOUR"
    )

    /**
     * Credit transaction patterns.
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

        "SUCCESSFULLY CREDITED",

        "DEPOSITED INTO",
        "DEPOSITED TO",
        "DEPOSITED IN",
        "DEPOSITED TOWARDS",

        "FUNDS RECEIVED",

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
}
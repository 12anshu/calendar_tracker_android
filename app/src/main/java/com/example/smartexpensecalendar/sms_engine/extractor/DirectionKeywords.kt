package com.example.smartexpensecalendar.sms_engine.extractor

object DirectionKeywords {

    val creditKeywords = setOf(

        "CREDITED",
        "CREDIT",

        "RECEIVED",
        "RECEIVE",

        "DEPOSITED",
        "DEPOSIT",

        "REFUND",
        "REFUNDED",

        "CASHBACK",

        "SALARY",

        "REVERSED",
        "REVERSAL",

        "TRANSFERRED TO YOUR ACCOUNT",

        "ADDED TO ACCOUNT",

        "INWARD",
        "INTEREST",
        "TOP-UP"
    )

    val debitKeywords = setOf(

        "DEBITED",
        "DEBIT",

        "SPENT",

        "PAID",

        "PURCHASE",

        "WITHDRAWN",
        "WITHDRAWAL",

        "AUTOPAY",

        "EMI",

        "CHARGED",

        "TRANSFERRED FROM",

        "SENT",

        "OUTWARD",
        "BILL PAY",
        "PAYMENT",
        "BY UPI",
        "ON CARD",
        "TXN OF",
        "TXN RS",
        "TXN INR",
        "POS TXN",
        "CARD USED",
        "USED AT",
        "SPENT ON",
        "PURCHASE OF",
        "PURCHASE MADE",
        "UPI TXN"
    )
}

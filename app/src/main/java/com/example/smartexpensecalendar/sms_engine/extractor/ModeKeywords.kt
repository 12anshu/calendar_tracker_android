package com.example.smartexpensecalendar.sms_engine.extractor

object ModeKeywords {

    val cardKeywords = setOf(
        "CARD",
        "CREDIT CARD",
        "DEBIT CARD",
        "VISA",
        "MASTERCARD",
        "RUPAY",
        "AMEX"
    )

    val upiKeywords = setOf(
        "UPI",
        "@YBL",
        "@OKSBI",
        "@OKHDFCBANK",
        "@PAYTM",
        "@APL",
        "VPA"
    )

    val bankTransferKeywords = setOf(
        "IMPS",
        "NEFT",
        "RTGS",
        "BANK TRANSFER",
        "TRANSFERRED"
    )

    val emiKeywords = setOf(
        "EMI",
        "INSTALLMENT",
        "CONVERTED INTO EMI"
    )

    val autoDebitKeywords = setOf(
        "AUTOPAY",
        "E-MANDATE",
        "AUTO DEBIT",
        "STANDING INSTRUCTION",
        "ECS"
    )

    val walletKeywords = setOf(
        "WALLET",
        "PAYTM WALLET",
        "AMAZON PAY",
        "PHONEPE WALLET",
        "MOBIKWIK"
    )

    val cashKeywords = setOf(
        "ATM",
        "CASH WITHDRAWAL",
        "CASH DEPOSIT",
        "WITHDRAWN AT ATM"
    )
}

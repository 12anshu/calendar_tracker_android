package com.example.smartexpensecalendar.sms.config

object ModePhrases {

    val cardPhrases = setOf(
        "CREDIT CARD",
        "DEBIT CARD",
        "CARD ENDING",
        "CARD XX",
        "CARD X",
        "ON CARD",
        "CARD PURCHASE",
        "CARD TRANSACTION",
        "CARD USED",
        "SWIPED AT",
        "POS TRANSACTION",
        "TAP AND PAY",
        "CONTACTLESS PAYMENT"
    )

    val upiPhrases = setOf(
        "BY UPI",
        "UPI PAYMENT",
        "UPI TRANSACTION",
        "UPI TXN",
        "UPI REF",
        "UPI REFERENCE",
        "VPA",
        "COLLECT REQUEST",
        "@YBL",
        "@OKSBI",
        "@OKHDFCBANK",
        "@PAYTM",
        "@APL"
    )

    val bankTransferPhrases = setOf(
        "IMPS TRANSFER",
        "NEFT TRANSFER",
        "RTGS TRANSFER",
        "FUND TRANSFER",
        "MONEY TRANSFER",
        "ACCOUNT TO ACCOUNT",
        "BENEFICIARY",
        "TRANSFERRED TO ACCOUNT",
        "TRANSFERRED FROM ACCOUNT",
        "BANK TRANSFER"
    )

    val emiPhrases = setOf(
        "EMI PAYMENT",
        "LOAN INSTALLMENT",
        "INSTALLMENT DUE",
        "CONVERTED INTO EMI",
        "EMI DEBITED"
    )

    val autoDebitPhrases = setOf(
        "AUTOPAY",
        "AUTO DEBIT",
        "E-MANDATE",
        "STANDING INSTRUCTION",
        "ECS DEBIT",
        "SI DEBIT"
    )

    val walletPhrases = setOf(
        "WALLET PAYMENT",
        "PAYTM WALLET",
        "AMAZON PAY",
        "PHONEPE WALLET",
        "MOBIKWIK WALLET",
        "WALLET DEBITED",
        "WALLET CREDITED"
    )

    val cashPhrases = setOf(
        "CASH WITHDRAWAL",
        "CASH DEPOSIT",
        "ATM WITHDRAWAL",
        "ATM CASH WITHDRAWAL",
        "CASH DEPOSITED"
    )
}

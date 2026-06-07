package com.example.smartexpensecalendar.sms.config


object FinancialEventKeywords {

    val creditCardPaymentKeywords = setOf(
        "credit card payment",
        "cc payment",
        "bill payment",
        "payment received towards your credit card",
        "payment received on credit card",
        "paid towards your credit card",
        "credited to your card ending",
        "online payment credited to your card"
    )

    val autoDebitKeywords = setOf(
        "ach",
        "nach",
        "ecs",
        "auto debit",
        "mandate"
    )

    val bankChargeKeywords = setOf(
        "annual fee",
        "service charge",
        "processing fee",
        "non-maintenance fee",
        "penalty",
        "charge"
    )

    val investmentKeywords = setOf(
        "sip",
        "mutual fund",
        "nps",
        "demat",
        "stock purchase",
        "fd booked",
        "rd installment"
    )

    val interestKeywords = setOf(
        "interest credit",
        "interest paid",
        "interest credited"
    )

    val refundKeywords = setOf(
        "refund",
        "refunded",
        "credited back",
        "cashback",
        "reversal",
        "reversed"
    )
}

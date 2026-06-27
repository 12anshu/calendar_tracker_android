package com.example.smartexpensecalendar.sms_engine.merchant

object MerchantPatterns {

    val AFTER_TO_ANCHORS = listOf(
        " TO "
    )

    val AFTER_AT_ANCHORS = listOf(
        " AT "
    )

    val AFTER_BY_ANCHORS = listOf(
        " BY "
    )

    val AFTER_FROM_ANCHORS = listOf(
        " FROM "
    )

    val MERCHANT_STOP_WORDS = listOf(
        "ACCOUNT",
        "A/C",
        "CARD",
        "BANK",
        "UPI",
        "INR",
        "RS"
    )

    val UPI_HANDLE_HINTS = listOf(
        "@YBL",
        "@OKHDFC",
        "@OKICICI",
        "@PAYTM",
        "@APL",
        "@AXISBANK",
        "@IBL",
        "@SBI"
    )

    // =====================================================
    // TRANSACTION TRIGGERS
    // =====================================================

    val TRANSACTION_TRIGGERS = listOf(
        "SPENT",
        "TXN",
        "TRANSACTION",
        "PURCHASE",
        "PAID",
        "PAYMENT",
        "TRANSFERRED",
        "SENT",
        "RECEIVED",
        "REFUND",
        "CREDITED",
        "DEBITED",
        "WITHDRAWN",
        "WITHDRAWAL",
        "CASHBACK"
    )

    // =====================================================
    // MERCHANT LOCATORS
    // =====================================================

    val MERCHANT_LOCATORS = listOf(
        "AT",
        "@",
        "TO",
        "FROM",
        "ON",
        "BY"
    )
}
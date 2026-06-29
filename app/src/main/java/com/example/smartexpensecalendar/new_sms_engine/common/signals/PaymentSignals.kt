package com.example.smartexpensecalendar.new_sms_engine.common.signals

/**
 * Payment related vocabulary used across the SMS Engine.
 */
object PaymentSignals {

    /**
     * Card related indicators.
     */
    val CARD_INDICATORS = setOf(
        "CARD",
        "CRD",
        "CR",
        "CD",
        "DEBIT",
        "CREDIT",
        "DC",
        "CC",
        "VISA",
        "MASTERCARD",
        "RUPAY",
        "AMEX"
    )

    /**
     * UPI related indicators.
     */
    val UPI_INDICATORS = setOf(
        "UPI",
        "VPA",
        "GPAY",
        "GOOGLE PAY",
        "PHONEPE",
        "PAYTM",
        "BHIM",
        "MOBIKWIK",
        "@YBL",
        "@OKSBI",
        "@OKHDFCBANK",
        "@OKICICI",
        "@PAYTM",
        "@APL"
    )

    /**
     * Bank transfer indicators.
     */
    val BANK_TRANSFER_INDICATORS = setOf(
        "IMPS",
        "NEFT",
        "RTGS",
        "FUND TRANSFER",
        "MONEY TRANSFER",
        "BANK TRANSFER",
        "BENEFICIARY"
    )

    /**
     * Auto debit indicators.
     */
    val AUTO_DEBIT_INDICATORS = setOf(
        "AUTOPAY",
        "AUTO DEBIT",
        "E-MANDATE",
        "STANDING INSTRUCTION",
        "ECS",
        "SI DEBIT"
    )

    /**
     * Wallet indicators.
     */
    val WALLET_INDICATORS = setOf(
        "WALLET",
        "WLT",
        "PREPAID",
        "AMAZON PAY",
        "PAYTM",
        "PHONEPE",
        "MOBIKWIK"
    )

    /**
     * Cash transaction indicators.
     */
    val CASH_INDICATORS = setOf(
        "CASH",
        "ATM",
        "WITHDRAWAL",
        "DEPOSIT"
    )

    /**
     * Meal benefit indicators.
     */
    val MEAL_CARD_INDICATORS = setOf(
        "MEAL",
        "FOOD",
        "BENEFIT",
        "VOUCHER",
        "SODEXO",
        "PLUXEE",
        "ZETA",
        "EDENRED",
        "SWILE"
    )

    /**
     * Rewards and loyalty indicators.
     */
    val REWARD_INDICATORS = setOf(
        "POINTS",
        "REWARDS",
        "RP",
        "COINS",
        "MILES",
        "CASHBACK"
    )
}
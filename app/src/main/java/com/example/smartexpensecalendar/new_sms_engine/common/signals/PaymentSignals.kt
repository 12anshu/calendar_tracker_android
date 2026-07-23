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

        "DEBIT CARD",
        "CREDIT CARD",

        "DEBIT CD",
        "CREDIT CD",

        "DEBIT CRD",
        "CREDIT CRD",

        "DEBIT CR",
        "CREDIT CR",

        "DEBIT DC",
        "CREDIT CC",

        "VISA",
        "MASTERCARD",
        "RUPAY",
        "AMEX",

        // NEW

        "AMERICAN EXPRESS",
        "DINERS",
        "DINERS CLUB",
        "DISCOVER",
        "CARD TXN",
        "CARD PURCHASE",
        "POS",
        "POS TXN",
        "TAP & PAY",
        "CONTACTLESS"
    )

    val SHORT_CARD_INDICATORS = setOf(
        "CR",
        "CC",
        "CD",
        "DC"
    )
    /**
     * UPI related indicators.
     */
    val UPI_INDICATORS = setOf(

        "UPI",
        "UPI LITE",
        "VPA",

        "GPAY",
        "GOOGLE PAY",

        "PHONEPE",

        "PAYTM",

        "BHIM",

        "MOBIKWIK",

        "CRED",

        "SUPER MONEY",

        "@UPI",
        "@OKSBI",
        "@OKHDFCBANK",
        "@OKICICI",
        "@PAYTM",
        "@APL",
        "@YBL",
        "@IBL",
        "@AXL"
    )

    /**
     * Bank transfer indicators.
     */
    val BANK_TRANSFER_INDICATORS = setOf(

        "IMPS",
        "NEFT",
        "RTGS",
        "FT",

        "FUND TRANSFER",

        "MONEY TRANSFER",

        "BANK TRANSFER",

        "ACCOUNT TRANSFER",

        "TRANSFER",

        "BENEFICIARY",

        "A/C TRANSFER",

        "INWARD REMITTANCE",

        "OUTWARD REMITTANCE",

        "ACH",

        "WIRE",

        "SWIFT"
    )

    /**
     * Auto debit indicators.
     */
    val AUTO_DEBIT_INDICATORS = setOf(

        "AUTOPAY",

        "AUTO DEBIT",

        "AUTO-PAY",

        "AUTO PAYMENT",

        "MANDATE",

        "E-MANDATE",

        "NACH",

        "STANDING INSTRUCTION",

        "SI",

        "SI DEBIT",

        "ECS"
    )

    /**
     * Wallet indicators.
     */
    val WALLET_INDICATORS = setOf(

        "WALLET",

        "WLT",

        "PREPAID",

        "PAYTM WALLET",

        "AMAZON PAY",

        "MOBIKWIK",

        "FREECHARGE",

        "AIRTEL MONEY",

        "JIOMONEY",

        "OLA MONEY"
    )

    /**
     * Cash transaction indicators.
     */
    val CASH_INDICATORS = setOf(

        "CASH",

        "ATM",

        "ATM WDL",

        "ATM WD",

        "ATM CASH",

        "CASH WITHDRAWAL",

        "CASH DEPOSIT",
    )

    /**
     * Meal benefit indicators.
     */
    val MEAL_CARD_INDICATORS = setOf(

        "MEAL",

        "MEAL CARD",

        "FOOD",

        "FOOD CARD",

        "BENEFIT",

        "VOUCHER",

        "SODEXO",

        "PLUXEE",

        "ZETA",

        "EDENRED",

        "SWILE"
    )

    /**
     * Cheque related indicators.
     */
    val CHEQUE_INDICATORS = setOf(
        "CHEQUE",
        "CHQ",
        "CHQ NO",
        "INSTRUMENT",
        "CLEARING",
        "CHQ DEPOSIT",
        "CHECK",
        "CTS"
    )

    val ALL = setOf(
        CARD_INDICATORS,
        UPI_INDICATORS,
        BANK_TRANSFER_INDICATORS,
        AUTO_DEBIT_INDICATORS,
        WALLET_INDICATORS,
        CASH_INDICATORS,
        MEAL_CARD_INDICATORS,
        CHEQUE_INDICATORS
    ).flatten().toSet()
}
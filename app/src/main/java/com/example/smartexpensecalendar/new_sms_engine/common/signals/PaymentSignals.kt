package com.example.smartexpensecalendar.new_sms_engine.common.signals

/**
 * Payment related vocabulary used across the SMS Engine.
 */
object PaymentSignals {

    val BANK_SIGNALS = setOf(
        "BANK",
        "BK",
        "BNK",
        "BRANCH",
    )

    val ACCOUNT_SIGNALS = setOf(
        "ACCOUNT",
        "A/C",
        "ACC",
        "ACCT",
        "ACCNT",
        "ACCOUNT NO",
        "A/C NO",
        "SAVINGS",
        "CURRENT",
        "LOAN",
        "OVERDRAFT",
        "OD"
    )
        /**
     * Card related SIGNALS.
     */
    val CARD_SIGNALS = setOf(

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

    val SHORT_CARD_SIGNALS = setOf(
        "CR",
        "CC",
        "CD",
        "DC"
    )
    /**
     * UPI related SIGNALS.
     */
    val UPI_SIGNALS = setOf(

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
     * Bank transfer SIGNALS.
     */
    val BANK_TRANSFER_SIGNALS = setOf(

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

        "SWIFT",

        "SAVINGS",
        "CURRENT",
        "NRE",
        "NRO",
        "NACH",
        "ECS",
        "AEPS",
        "BBPS",
        "NET BANKING",
        "ONLINE TRANSFER",
        "MOBILE BANKING",
        "INTERNET BANKING",
        "TPT",
        "OWN A/C"
    )

    /**
     * Auto debit SIGNALS.
     */
    val AUTO_DEBIT_SIGNALS = setOf(

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
     * Wallet SIGNALS.
     */
    val WALLET_SIGNALS = setOf(

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
     * Cash transaction SIGNALS.
     */
    val CASH_SIGNALS = setOf(

        "CASH",

        "ATM",

        "ATM WDL",

        "ATM WD",

        "ATM CASH",

        "CASH WITHDRAWAL",

        "CASH DEPOSIT",
    )

    /**
     * Meal benefit SIGNALS.
     */
    val MEAL_CARD_SIGNALS = setOf(

        "MEAL",

        "MEAL CARD",

        "FOOD",

        "FOOD CARD",

        "VOUCHER",

        "SODEXO",

        "PLUXEE",

        "ZETA",

        "EDENRED",

        "SWILE",
        "TICKET RESTAURANT",
        "TICKET MEAL",
        "TICKET FOOD",
        "TICKET COMPLIMENTS",
        "FOODPLUS",
        "FOOD PLUS"
    )

    /**
     * Cheque related SIGNALS.
     */
    val CHEQUE_SIGNALS = setOf(
        "CHEQUE",
        "CHQ",
        "CHQ NO",
        "INSTRUMENT",
        "CLEARING",
        "CHQ DEPOSIT",
        "CHECK",
        "CTS"
    )

    val REPAYMENT_SIGNALS = setOf(
        "EMI",
        "INSTALLMENT",
        "INSTALMENT",
        "REPAYMENT"
    )

    val ALL = setOf(
        BANK_SIGNALS,
        ACCOUNT_SIGNALS,
        CARD_SIGNALS,
        SHORT_CARD_SIGNALS,
        UPI_SIGNALS,
        BANK_TRANSFER_SIGNALS,
        AUTO_DEBIT_SIGNALS,
        WALLET_SIGNALS,
        CASH_SIGNALS,
        MEAL_CARD_SIGNALS,
        CHEQUE_SIGNALS,
        REPAYMENT_SIGNALS
    ).flatten().toSet()
}
package com.example.smartexpensecalendar.new_sms_engine.common.signals

/**
 * Banking related vocabulary used across the SMS Engine.
 *
 * These are semantic signals rather than detection rules.
 */
object BankingSignals {

    /**
     * Bank names.
     */
    val BANKS = setOf(
        "HDFC",
        "ICICI",
        "AXIS",
        "SBI",
        "KOTAK",
        "YES BANK",
        "IDFC",
        "RBL",
        "FEDERAL",
        "CITI",
        "HSBC",
        "SCB",
        "AMEX",
        "DBS",
        "DIGIBANK",
        "STANDARD CHARTERED",
        "BARODA",
        "PNB",
        "CANARA",
        "UNION BANK",
        "INDIAN BANK",
        "BOI",
        "UCO",
        "BOM",
        "IDBI",
        "IOB",
        "PSB",
        "AU BANK",
        "EQUITAS",
        "UJJIVAN",
        "FINO",
        "NSDL",
        "IPPB",
        "PAYTM",
        "AIRTEL",
        "JIO",
        "SARASWAT",
        "COSMOS",
        "TJSB",
        "SVC",
        "NKGSB"
    )

    /**
     * Banking institution indicators.
     */
    val BANK_INDICATORS = setOf(
        "BANK",
        "BK",
        "BNK",
        "BRANCH",
        "BR."
    )

    /**
     * Account related indicators.
     */
    val ACCOUNT_INDICATORS = setOf(
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
     * Financial SMS sender identifiers.
     *
     * NOTE:
     * These are SMS sender IDs, not bank names.
     */
    val FINANCIAL_SENDER_SIGNALS = setOf(
        "HDFCBK",
        "ICICIB",
        "SBIINB",
        "AXISBK",
        "KOTAKB",
        "IDFCFB",
        "YESBNK",
        "RBLBNK",
        "FEDBNK",
        "PNBSMS",
        "CANBNK",
        "UNIONB",
        "BOBTXN",
        "HSBCBK",
        "SCBANK",
        "CITIBK",
        "AMEXIN",
        "PAYTM",
        "PHONEPE",
        "GPAY",
        "BHIMUP",
        "AMAZON",
        "MOBIKWIK"
    )
}
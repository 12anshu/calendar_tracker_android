package com.example.smartexpensecalendar.sms_engine.detector

enum class TransactionClass {

    CARD_SPEND,

    ACCOUNT_DEBIT,

    ACCOUNT_CREDIT,

    TRANSFER,

    REFUND,

    EMI,

    CARD_PAYMENT,

    BANK_CHARGE,

    CASH_WITHDRAWAL,

    SUBSCRIPTION,

    MANDATE,

    UNKNOWN
}

package com.example.smartexpensecalendar.domain.model

enum class FinancialEventType {

    EXPENSE,

    INCOME,

    REFUND,

    TRANSFER,

    CREDIT_CARD_PAYMENT,

    CREDIT_CARD_SPEND,

    EMI_PAYMENT,

    CASH_WITHDRAWAL,

    CASH_DEPOSIT,

    INVESTMENT,

    UNKNOWN
}

fun FinancialEventType.requiresMerchant(): Boolean {
    return when (this) {
        FinancialEventType.EXPENSE,
        FinancialEventType.CREDIT_CARD_SPEND -> true

        else -> false
    }
}

package com.example.smartexpensecalendar.domain.model

enum class FinancialEventType {

    EXPENSE,

    INCOME,

    REFUND,

    TRANSFER,

    CREDIT_CARD_PAYMENT,

    CREDIT_CARD_SPEND,

    EMI_PAYMENT,

    EMI_CONVERSION,

    MEAL_CARD,

    CASH_WITHDRAWAL,

    CASH_DEPOSIT,

    INVESTMENT,

    SALARY,

    CASHBACK,

    UNKNOWN
}

fun FinancialEventType.requiresMerchant(): Boolean {
    return when (this) {
        FinancialEventType.EXPENSE,
        FinancialEventType.CREDIT_CARD_SPEND,
        FinancialEventType.MEAL_CARD -> true

        else -> false
    }
}

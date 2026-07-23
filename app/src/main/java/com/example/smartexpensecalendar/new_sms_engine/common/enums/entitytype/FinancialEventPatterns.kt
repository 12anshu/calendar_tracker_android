package com.example.smartexpensecalendar.new_sms_engine.common.enums.entitytype

object FinancialEventPatterns {

    val PURCHASE = listOf(

        "spent on",
        "spent using",
        "paid to",
        "paid at",
        "purchase at",
        "purchase using"
    )

    val TRANSFER = listOf(

        "transferred to",
        "transferred from",
        "fund transfer",
        "upi transfer",
        "account transfer"
    )

    val PAYMENT = listOf(

        "credited to your card",
        "payment towards",
        "payment of",
        "loan payment",
        "emi payment"
    )

    val ATM_WITHDRAWAL = listOf(

        "cash withdrawn",
        "withdrawn from atm"
    )

    val CASH_DEPOSIT = listOf(

        "cash deposited"
    )

    val REFUND = listOf(

        "refund initiated",
        "refund processed",
        "refund credited",
        "amount reversed"
    )

    val SALARY = listOf(

        "salary credited",
        "salary deposited"
    )

    val INTEREST = listOf(

        "interest credited"
    )

    val CASHBACK = listOf(

        "cashback credited"
    )

    val CHARGE = listOf(

        "annual fee",
        "service charge",
        "processing fee",
        "late fee"
    )
}
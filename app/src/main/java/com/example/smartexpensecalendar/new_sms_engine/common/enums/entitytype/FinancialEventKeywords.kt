package com.example.smartexpensecalendar.new_sms_engine.common.enums.entitytype

object FinancialEventKeywords {

    val PURCHASE = setOf(
        "SPENT",
        "PURCHASE",
        "PURCHASED",
        "PAID",
        "PAYMENT",
        "SHOPPING"
    )

    val TRANSFER = setOf(
        "TRANSFER",
        "TRANSFERRED",
        "IMPS",
        "NEFT",
        "RTGS",
        "UPI",
        "UPIID",
        "VPA"
    )

    val PAYMENT = setOf(
        "CARD PAYMENT",
        "PAYMENT RECEIVED",
        "PAYMENT OF",
        "PAYMENT TOWARDS",
        "EMI",
        "LOAN PAYMENT",
        "BILL PAYMENT"
    )

    val ATM_WITHDRAWAL = setOf(
        "ATM",
        "WITHDRAWN",
        "CASH WITHDRAWAL"
    )

    val CASH_DEPOSIT = setOf(
        "CASH DEPOSIT",
        "DEPOSITED"
    )

    val REFUND = setOf(
        "REFUND",
        "REFUNDED",
        "REVERSED",
        "REVERSAL"
    )

    val SALARY = setOf(
        "SALARY",
        "PAYROLL"
    )

    val INTEREST = setOf(
        "INTEREST"
    )

    val CASHBACK = setOf(
        "CASHBACK",
        "REWARD",
        "REWARDS"
    )

    val CHARGE = setOf(
        "CHARGE",
        "CHARGES",
        "FEE",
        "PENALTY",
        "GST",
        "ANNUAL FEE",
        "SERVICE CHARGE",
        "PROCESSING FEE"
    )
}
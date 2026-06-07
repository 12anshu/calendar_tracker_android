package com.example.smartexpensecalendar.sms_engine.extractor

object EventTypeKeywords {

    val incomeKeywords = setOf(
        "SALARY",
        "PAYROLL",
        "INTEREST CREDITED",
        "INTEREST",
        "CASHBACK",
        "RECEIVED",
        "CREDITED",
        "DEPOSITED"
    )

    val refundKeywords = setOf(
        "REFUND",
        "REFUNDED",
        "REVERSAL",
        "REVERSED"
    )

    val transferKeywords = setOf(
        "TRANSFERRED",
        "IMPS",
        "NEFT",
        "RTGS",
        "FUND TRANSFER"
    )

    val creditCardPaymentKeywords = setOf(
        "CREDIT CARD PAYMENT",
        "CARD BILL PAYMENT",
        "PAID TOWARDS YOUR CREDIT CARD",
        "PAYMENT RECEIVED",
        "RECEIVED TOWARDS",
        "CREDIT CARD DUES",
        "PAYMENT RECEIVED FOR YOUR CARD",
        "PAYMENT RECEIVED ON",
        "RECEIVED TOWARDS YOUR CREDIT CARD",
        "PAYMENT OF INR",
        "PAYMENT OF RS",
        "PAYMENT HAS BEEN RECEIVED"
    )

    val creditCardSpendKeywords = setOf(
        "ON CARD",
        "CARD PURCHASE",
        "CARD USED",
        "SPENT",
        "PURCHASED",
        "SWIPED"
    )

    val emiKeywords = setOf(
        "EMI",
        "INSTALLMENT",
        "LOAN PAYMENT"
    )

    val investmentKeywords = setOf(
        "MUTUAL FUND",
        "SYSTEMATIC INVESTMENT PLAN",
        "DEMAT",
        "FIXED DEPOSIT",
        "RECURRING DEPOSIT",
        "PPF",
        "NPS"
    )

    val cashWithdrawalKeywords = setOf(
        "ATM WITHDRAWAL",
        "CASH WITHDRAWAL",
        "WITHDRAWAL",
        "WITHDRAWN",
        "CASH WDL"
    )

    val cashDepositKeywords = setOf(
        "CASH DEPOSIT",
        "DEPOSITED"
    )
}

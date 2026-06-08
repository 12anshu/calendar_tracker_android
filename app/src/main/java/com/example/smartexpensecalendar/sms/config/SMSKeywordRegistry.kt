package com.example.smartexpensecalendar.sms.config
import com.example.smartexpensecalendar.sms.config.MessageTypeKeywords

object SMSKeywordRegistry {

    val financialSignals = setOf(
        // Currency
        "RS",
        "INR",
        // Transaction words
        "TRANSACTION",
        "TXN",
        "PAYMENT",
        // Money movement
        "DEBITED",
        "CREDITED",
        "SPENT",
        "PAID",
        "RECEIVED",
        "TRANSFERRED",
        // Banking
        "ACCOUNT",
        "A/C",
        "BALANCE",
        // Cards
        "CARD",
        "CREDIT CARD",
        "DEBIT CARD",
        // Channels
        "UPI",
        "IMPS",
        "NEFT",
        "RTGS",
        // Events
        "EMI",
        "REFUND",
        "REVERSAL",
        "SALARY",
        "INTEREST",
        "CASHBACK",
        // Investments
        "SIP",
        "MUTUAL FUND",
        "NPS",
        "DEMAT"
    )

    val expenseKeywords = setOf(
        "SPENT",
        "PAID",
        "PURCHASE",
        "DEBITED",
        "PURCHASED",
        "USED AT",
        "TXN DONE",
        "PAYMENT SUCCESSFUL"
    )

    val incomeKeywords = setOf(
        "CREDITED",
        "RECEIVED",
        "DEPOSITED",
        "CREDIT SUCCESSFUL"
    )

    val transferKeywords = setOf(
        "TRANSFERRED",
        "SENT",
        "IMPS",
        "NEFT",
        "RTGS",
        "ACCOUNT TO ACCOUNT",
        "FUND TRANSFER"
    )

    val bankTransferKeywords = setOf(
        "IMPS",
        "NEFT",
        "RTGS",
        "FUND TRANSFER",
        "ACCOUNT TO ACCOUNT",
        "TRANSFERRED",
        "BENEFICIARY"
    )

    val refundKeywords = setOf(
        "REFUND",
        "REFUNDED",
        "REVERSED",
        "REVERSAL"
    )

    val salaryKeywords = setOf(
        "SALARY",
        "PAYROLL",
        "SAL CREDIT"
    )

    val investmentKeywords = setOf(
        "SIP",
        "MUTUAL FUND",
        "NPS",
        "STOCK PURCHASE",
        "DEMAT",
        "FD BOOKED",
        "RD INSTALLMENT"
    )

    val interestKeywords = setOf(
        "INTEREST CREDIT",
        "INTEREST PAID"
    )

    val cashbackKeywords = setOf(
        "CASHBACK",
        "REWARD",
        "REWARD POINTS"
    )

    val feeKeywords = setOf(
        "FEE",
        "CHARGE",
        "PENALTY",
        "NON-MAINTENANCE",
        "SERVICE CHARGE",
        "ANNUAL FEE"
    )

    val emiKeywords = setOf(
        "EMI",
        "INSTALLMENT",
        "LOAN PAYMENT"
    )

    val cardPaymentKeywords = setOf(
        "PAYMENT RECEIVED",
        "CREDIT CARD PAYMENT",
        "PAID TOWARDS YOUR CREDIT CARD",
        "PAYMENT RECEIVED TOWARDS YOUR CREDIT CARD",
        "PAYMENT RECEIVED ON CREDIT CARD",
        "PAID TOWARDS YOUR CREDIT CARD",
        "CREDITED TO YOUR CARD ENDING",
        "ONLINE PAYMENT CREDITED TO YOUR CARD"
    )

    val upiKeywords = setOf(
        "UPI",
        "UPI REF",
        "UPI ID",
        "VPA",
        "UPI TRANSACTION",
        "UPI PAYMENT",
        "UPI TRANSFER"
    )

    val cardKeywords = setOf(
        "CARD",
        "CREDIT CARD",
        "DEBIT CARD",
        "CARD ENDING",
        "CARD XX",
        "CARD X",
        "VISA",
        "MASTERCARD",
        "RUPAY",
        "AMEX"
    )

    val negativeFinancialKeywords = setOf(
        "OTP",
        "LOGIN OTP",
        "VERIFICATION CODE",
        "PASSWORD RESET",
        "AUTHENTICATION CODE",
        "SECURE CODE",
        "BOOKING CONFIRMED",
        "CONSUMED",
        "VERIFICATION",
        "LOGIN",
        "PASSWORD",
        "ACCESS CODE",
        "PASSCODE",
        "TOKEN",
        "SECRET CODE",
        "M-PIN",
        "T-PIN",
        "VERIFY",
        "AUTHENTICATE",
        "AUTHORIZE",
        "SECURITY CODE",
        "ONE TIME PASSWORD",
        "TEMPORARY PASSWORD",
        "LOGIN ATTEMPT",
        "OTP IS",
        "DO NOT SHARE",
        "SHARE THIS OTP",
        "CONFIDENTIAL",
        "PACK VALID",
        "SMS COST",
        "CALLCOST",
        "DURATION",
        "DATA USED",
        "REMAINING DATA",
        "BALANCE EXPIRED",
        "PLAN EXPIRED",
        "VALIDITY EXPIRED",
        "RECHARGE DUE",
        "PACK EXPIRED"
    )

    val creditKeywords = setOf(
        "DEPOSITED",
        "CREDITED",
        "RECEIVED",
        "PAYMENT RECEIVED",
        "SALARY",
        "SALARY CREDIT",
        "CASHBACK",
        "REFUND",
        "REFUNDED",
        "INTEREST CREDITED",
        "INTEREST PAID",
        "FUNDS RECEIVED",
        "AMOUNT RECEIVED",
        "NEFT CR",
        "RTGS CR",
        "IMPS CR",
        "INWARD REMITTANCE",
        "CREDIT TRANSACTION"
    )

    val debitKeywords = setOf(
        "DEBITED",
        "SPENT",
        "PAID",
        "WITHDRAWN",
        "PURCHASE",
        "PURCHASED",
        "TXN DONE",
        "PAYMENT SUCCESSFUL",
        "ATM WDL",
        "ATM WITHDRAWAL",
        "CASH WITHDRAWAL",
        "DEBIT TRANSACTION",
        "POS",
        "SWIPED"
    )

    val financialKeywords = financialSignals

    val settlementKeywords = cardPaymentKeywords

    val ignoreKeywords =
        negativeFinancialKeywords +
                MessageTypeKeywords.obligationKeywords +
                MessageTypeKeywords.promotionalKeywords +
                MessageTypeKeywords.informationKeywords
}

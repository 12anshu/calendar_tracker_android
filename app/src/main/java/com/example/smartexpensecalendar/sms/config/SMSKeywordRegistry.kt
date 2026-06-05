package com.example.smartexpensecalendar.sms.config

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

        "PAID TOWARDS YOUR CREDIT CARD"
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

    val obligationKeywords = setOf(

        "DUE",

        "TOTAL AMOUNT DUE",

        "MINIMUM AMOUNT DUE",

        "PAY BEFORE",

        "PAY BY",

        "COLLECT REQUEST",

        "MANDATE CREATED",

        "AUTOPAY REGISTRATION"
    )

    val informationKeywords = setOf(

        "CURRENT BALANCE",

        "AVAILABLE BALANCE",

        "STATEMENT GENERATED",

        "CREDIT LIMIT",

        "ACCOUNT UPDATED"
    )

    val promotionalKeywords = setOf(

        "PRE-APPROVED",

        "LOAN OFFER",

        "CASHBACK OFFER",

        "ELIGIBLE FOR",

        "LIFETIME FREE"
    )

    val negativeFinancialKeywords = setOf(

        "OTP",

        "LOGIN OTP",

        "VERIFICATION CODE",

        "PASSWORD RESET",

        "AUTHENTICATION CODE"
    )
}
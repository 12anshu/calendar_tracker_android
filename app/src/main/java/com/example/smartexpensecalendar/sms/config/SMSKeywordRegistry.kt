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
        "MINIMUM AMOUNT DUE",
        "EMI DUE",
        "REPAYMENT",
        "PAY BEFORE",
        "PAYMENT DUE",
        "OUTSTANDING",
        "BOUNCE CHARGE",
        "COLLECT REQUEST",
        "MANDATE CREATED",
        "AUTOPAY REGISTRATION"
    )

    val negativeFinancialKeywords = setOf(
        "OTP",
        "LOGIN OTP",
        "VERIFICATION CODE",
        "PASSWORD RESET",
        "AUTHENTICATION CODE"
    )

    val transactionKeywords = setOf(
        "DEBITED",
        "CREDITED",
        "SPENT",
        "PAID",
        "RECEIVED",
        "WITHDRAWN",
        "PURCHASE",
        "CASHBACK CREDITED",
        "REFUND PROCESSED",
        "SPENT",
        "PURCHASE",
        "PURCHASED",
        "POS",
        "ATM",
        "ATM WDL",
        "ATM WITHDRAWAL",
        "CASH WITHDRAWAL",
        "UPI PAYMENT",
        "PAID VIA",
        "PAID USING",
        "MERCHANT",
        "CARD USED",
        "CARD PURCHASE",
        "DEBIT TRANSACTION",
        "CARD",
        "ON CARD",
        "BANK CARD",
        "CREDIT CARD",
        "DEBIT CARD",
        "PURCHASE",
        "POS",
        "SWIPED",
        "PAYMENT OF RS",
        "PAYMENT SUCCESSFUL",
        "TRANSACTION SUCCESSFUL",
        "TRANSFER SUCCESSFUL",
        "REFUND SUCCESSFUL",
        "RECHARGE SUCCESSFUL",
        "ADDED TO",
        "TRANSFERRED",
        "RECEIVED",
        "WITHDRAWN"
    )

    val informationKeywords = setOf(
        "STATEMENT GENERATED",
        "BALANCE UPDATED",
        "UPI REGISTRATION",
        "UPI LINK REQUEST",
        "CARD UPDATED",
        "INTEREST UPDATED",
        "TDS",
        "E-STATEMENT",
        "ACCOUNT SUMMARY",
        "CURRENT BALANCE",
        "AVAILABLE BALANCE",
        "CREDIT LIMIT",
        "ACCOUNT UPDATED",
        "REGISTRATION",
        "REGISTERED",
        "UPI REGISTRATION",
        "TDS",
        "TAX DEDUCTED",
        "STATEMENT GENERATED",
        "STATEMENT AVAILABLE",
        "KYC",
        "KYC UPDATED",
        "ACCOUNT UPDATED",
        "PROFILE UPDATED",
        "MOBILE UPDATED",
        "EMAIL UPDATED",
        "CARD ACTIVATED",
        "CARD BLOCKED",
        "CARD UNBLOCKED",
        "SERVICE REQUEST",
        "STATEMENT",
        "DUPLICATE STATEMENT",
        "E-STATEMENT",
        "CARDMEMBER",
        "PASSWORD FORMAT",
        "UPDATED TERMS",
        "ACCOUNT UPDATED",
        "ACTIVATION",
        "ACTIVATED",
        "REGISTERED",
        "REGISTRATION",
        "PIN SET",
        "PASSWORD RESET",
        "DEVICE REGISTERED"
    )

    val promotionalKeywords = setOf(
        "PRE-APPROVED",
        "LOAN OFFER",
        "CASHBACK OFFER",
        "ELIGIBLE FOR",
        "LIFETIME FREE",
        "PERSONAL LOAN",
        "APPLY NOW",
        "ELIGIBLE",
        "UPGRADE YOUR CARD",
        "INSTANT LOAN",
        "OFFER",
        "EXCLUSIVE OFFER"
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
    val financialKeywords = financialSignals

    val settlementKeywords = cardPaymentKeywords

    val ignoreKeywords = negativeFinancialKeywords +
            obligationKeywords +
            promotionalKeywords +
            informationKeywords
}

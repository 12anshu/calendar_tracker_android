package com.example.smartexpensecalendar.new_sms_engine.common.patterns

/**
 * Reusable financial event language patterns.
 *
 * These patterns identify the business event represented
 * by an SMS rather than its direction.
 */
object EventPatterns {

    /**
     * Refund related patterns.
     */
    val REFUND_PATTERNS = setOf(

        "REFUND INITIATED",
        "REFUND PROCESSED",
        "REFUND COMPLETED",
        "REFUND SUCCESSFUL",
        "REFUND CREDITED",

        "AMOUNT REVERSED",
        "REVERSAL PROCESSED",
        "CHARGE REVERSAL"
    )

    /**
     * Credit card payment patterns.
     */
    val CREDIT_CARD_PAYMENT_PATTERNS = setOf(

        "CREDIT CARD PAYMENT",
        "CARD BILL PAYMENT",

        "PAID TOWARDS YOUR CREDIT CARD",

        "PAYMENT RECEIVED FOR YOUR CARD",

        "PAYMENT RECEIVED ON",

        "RECEIVED TOWARDS YOUR CREDIT CARD",

        "PAYMENT HAS BEEN RECEIVED",

        "RECEIVED ON CREDIT CARD"
    )

    /**
     * Transfer related patterns.
     */
    val TRANSFER_PATTERNS = setOf(

        "FUND TRANSFER",
        "MONEY TRANSFER",
        "ACCOUNT TO ACCOUNT",

        "BENEFICIARY TRANSFER",

        "TRANSFERRED TO",
        "TRANSFERRED FROM",

        "IMPS TRANSFER",
        "NEFT TRANSFER",
        "RTGS TRANSFER",
        "UPI TRANSFER"
    )

    /**
     * EMI related patterns.
     */
    val EMI_PATTERNS = setOf(

        "EMI PAYMENT",
        "EMI DEBITED",
        "EMI DEDUCTED",

        "LOAN INSTALLMENT",
        "INSTALLMENT DEDUCTED",

        "TOWARDS LOAN",

        "LOAN REPAYMENT",

        "EMI PROCESSED"
    )

    /**
     * Investment related patterns.
     */
    val INVESTMENT_PATTERNS = setOf(

        "MUTUAL FUND PURCHASE",

        "SIP INSTALLMENT",
        "SIP PAYMENT",

        "FD BOOKED",

        "RD INSTALLMENT",

        "NPS CONTRIBUTION",

        "DEMAT PURCHASE",

        "STOCK PURCHASE"
    )
}
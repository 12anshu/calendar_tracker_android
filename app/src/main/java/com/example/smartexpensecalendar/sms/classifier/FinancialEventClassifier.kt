package com.example.smartexpensecalendar.sms.classifier

import com.example.smartexpensecalendar.domain.model.FinancialEventType
import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.sms.config.FinancialEventKeywords

object FinancialEventClassifier {

    fun classify(
        sms: String,
        direction: TransactionDirection
    ): FinancialEventType {

        val lower = sms.lowercase()

        return when {

            isRefund(lower) ->
                FinancialEventType.REFUND

            isInvestment(lower) ->
                FinancialEventType.INVESTMENT

            lower.contains("salary") ->
                FinancialEventType.INCOME

            isInterestCredit(lower) ->
                FinancialEventType.INCOME

            lower.contains("emi") ->
                FinancialEventType.EMI_PAYMENT

            isCashWithdrawal(lower) ->
                FinancialEventType.CASH_WITHDRAWAL

            isCreditCardPayment(lower) ->
                FinancialEventType.CREDIT_CARD_PAYMENT

            isTransfer(lower) ->
                FinancialEventType.TRANSFER

            isCardSpend(lower, direction) ->
                FinancialEventType.CREDIT_CARD_SPEND

            isBankCharge(lower) ->
                FinancialEventType.EXPENSE

            isAutoDebit(lower) ->
                FinancialEventType.EXPENSE

            direction == TransactionDirection.CREDIT ->
                FinancialEventType.INCOME

            direction == TransactionDirection.DEBIT ->
                FinancialEventType.EXPENSE

            else ->
                FinancialEventType.UNKNOWN
        }
    }

    private fun isCreditCardPayment(
        text: String
    ): Boolean {
        return FinancialEventKeywords
            .creditCardPaymentKeywords
            .any { text.contains(it) }
    }

    private fun isInvestment(
        text: String
    ): Boolean {
        return FinancialEventKeywords
            .investmentKeywords
            .any { text.contains(it) }
    }

    private fun isInterestCredit(
        text: String
    ): Boolean {
        return FinancialEventKeywords
            .interestKeywords
            .any { text.contains(it) }
    }

    private fun isRefund(
        text: String
    ): Boolean {
        return FinancialEventKeywords
            .refundKeywords
            .any { text.contains(it) }
    }

    private fun isBankCharge(
        text: String
    ): Boolean {
        return FinancialEventKeywords
            .bankChargeKeywords
            .any { text.contains(it) }
    }

    private fun isAutoDebit(
        text: String
    ): Boolean {
        return FinancialEventKeywords
            .autoDebitKeywords
            .any { text.contains(it) }
    }

    private fun isTransfer(
        text: String
    ): Boolean {
        return listOf("neft", "imps", "rtgs", "transfer").any { text.contains(it) }
    }

    private fun isCashWithdrawal(
        text: String
    ): Boolean {
        return text.contains("withdrawal") ||
                text.contains("withdrawn") ||
                text.contains("cash wdl")
    }

    private fun isCardSpend(
        text: String,
        direction: TransactionDirection
    ): Boolean {

        if (direction != TransactionDirection.DEBIT) {
            return false
        }

        return text.contains("credit card") ||
                text.contains("debit card") ||
                text.contains("card xx") ||
                text.contains("card x") ||
                text.contains("card ending") ||
                text.contains("spent using") ||
                text.contains("spent on")
    }
}

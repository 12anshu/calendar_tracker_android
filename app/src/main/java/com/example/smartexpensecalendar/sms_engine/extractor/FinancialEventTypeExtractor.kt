package com.example.smartexpensecalendar.sms_engine.extractor

import com.example.smartexpensecalendar.domain.model.FinancialEventType
import com.example.smartexpensecalendar.domain.model.TransactionDirection
import com.example.smartexpensecalendar.domain.model.TransactionMode
import com.example.smartexpensecalendar.sms.config.EventTypePhrases

object FinancialEventTypeExtractor {

    fun extract(
        smsText: String,
        direction: TransactionDirection,
        mode: TransactionMode
    ): FinancialEventType {

        val text = smsText.uppercase()

        // Phrases
        if (
            containsAny(
                text,
                EventTypePhrases.refundPhrases
            )
        ) {
            return FinancialEventType.REFUND
        }

        if (
            containsAny(
                text,
                EventTypePhrases.cashWithdrawalPhrases
            )
        ) {
            return FinancialEventType.CASH_WITHDRAWAL
        }

        if (
            containsAny(
                text,
                EventTypePhrases.cashDepositPhrases
            )
        ) {
            return FinancialEventType.CASH_DEPOSIT
        }

        if (
            containsAny(
                text,
                EventTypePhrases.creditCardPaymentPhrases
            )
        ) {
            return FinancialEventType.CREDIT_CARD_PAYMENT
        }

        if (
            containsAny(
                text,
                EventTypePhrases.emiPhrases
            )
        ) {
            return FinancialEventType.EMI_PAYMENT
        }

        if (
            containsAny(
                text,
                EventTypePhrases.investmentPhrases
            )
        ) {
            return FinancialEventType.INVESTMENT
        }

        if (
            containsAny(
                text,
                EventTypePhrases.transferPhrases
            ) || text.contains("SENT FROM") || text.contains("TO A/C")
        ) {
            return FinancialEventType.TRANSFER
        }

        if (
            containsAny(
                text,
                EventTypePhrases.incomePhrases
            )
        ) {
            return FinancialEventType.INCOME
        }

        if (
            containsAny(
                text,
                EventTypePhrases.creditCardSpendPhrases
            )
        ) {
            return FinancialEventType.CREDIT_CARD_SPEND
        }

        if (
            text.contains("PAYMENT OF") &&
            text.contains("RECEIVED") &&
            text.contains("CREDIT CARD")
        ) {
            return FinancialEventType.CREDIT_CARD_PAYMENT
        }

        // Keywords
        if (
            containsAny(
                text,
                EventTypeKeywords.refundKeywords
            )
        ) {
            return FinancialEventType.REFUND
        }

        if (
            containsAny(
                text,
                EventTypeKeywords.cashWithdrawalKeywords
            )
        ) {
            return FinancialEventType.CASH_WITHDRAWAL
        }

        if (
            containsAny(
                text,
                EventTypeKeywords.cashDepositKeywords
            )
        ) {
            return FinancialEventType.CASH_DEPOSIT
        }

        if (
            containsAny(
                text,
                EventTypeKeywords.creditCardPaymentKeywords
            )
        ) {
            return FinancialEventType.CREDIT_CARD_PAYMENT
        }

        if (
            containsAny(
                text,
                EventTypeKeywords.emiKeywords
            )
        ) {
            return FinancialEventType.EMI_PAYMENT
        }

        if (
            containsAny(
                text,
                EventTypeKeywords.investmentKeywords
            )
        ) {
            return FinancialEventType.INVESTMENT
        }

        if (
            containsAny(
                text,
                EventTypeKeywords.transferKeywords
            )
        ) {
            return FinancialEventType.TRANSFER
        }

        if (
            containsAny(
                text,
                EventTypeKeywords.incomeKeywords
            )
        ) {
            return FinancialEventType.INCOME
        }

        if (
            containsAny(
                text,
                EventTypeKeywords.creditCardSpendKeywords
            )
        ) {
            return FinancialEventType.CREDIT_CARD_SPEND
        }

        //
        if (
            mode == TransactionMode.CARD &&
            direction == TransactionDirection.DEBIT
        ) {
            return FinancialEventType.CREDIT_CARD_SPEND
        }

        if (direction == TransactionDirection.DEBIT) {
            return FinancialEventType.EXPENSE
        }

        if (direction == TransactionDirection.CREDIT) {
            return FinancialEventType.INCOME
        }

        return FinancialEventType.UNKNOWN
    }

    private fun containsAny(
        text: String,
        keywords: Set<String>
    ): Boolean {

        return keywords.any {
            text.contains(it)
        }
    }
}

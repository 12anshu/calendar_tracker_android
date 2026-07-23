package com.example.smartexpensecalendar.new_sms_engine.extraction.paymentmode

import com.example.smartexpensecalendar.new_sms_engine.common.confidence.ConfidenceCalculator
import com.example.smartexpensecalendar.new_sms_engine.common.enums.paymentmode.PaymentPatterns
import com.example.smartexpensecalendar.new_sms_engine.extraction.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.extraction.paymentmode.models.PaymentMode
import com.example.smartexpensecalendar.new_sms_engine.extraction.paymentmode.models.PaymentModeResult

class PaymentModeExtractor {

    fun extract(
        context: ExtractionContext
    ): PaymentModeResult {

        val message = context.message

        val scores = mutableMapOf<PaymentMode, Int>()

        scoreUpi(message, scores)
        scoreCard(message, scores)
        scoreBankTransfer(message, scores)
        scoreWallet(message, scores)
        scoreAutoDebit(message, scores)
        scoreMealCard(message, scores)
        scoreCash(message, scores)
        scoreCheque(message, scores)

        if (scores.isEmpty()) {
            return PaymentModeResult(
                mode = PaymentMode.UNKNOWN,
                confidence = 0f
            )
        }

        val sorted = scores.entries
            .sortedByDescending { it.value }

        val winner = sorted.first()

        val runnerUp =
            sorted.getOrNull(1)?.value ?: 0

        return PaymentModeResult(
            mode = winner.key,
            confidence = ConfidenceCalculator.fromScores(
                winner = winner.value,
                competitor = runnerUp
            )
        )
    }

    private fun scoreUpi(
        message: String,
        scores: MutableMap<PaymentMode, Int>
    ) {

        if (PaymentPatterns.UPI_ID_PATTERN.containsMatchIn(message)) {
            scores.incrementScore(
                PaymentMode.UPI,
                PaymentModeScores.UPI_ID
            )
        }

        if (PaymentPatterns.UPI_PATTERN.containsMatchIn(message)) {
            scores.incrementScore(
                PaymentMode.UPI,
                PaymentModeScores.UPI
            )
        }
    }

    private fun scoreCard(
        message: String,
        scores: MutableMap<PaymentMode, Int>
    ) {

        if (PaymentPatterns.CARD_PATTERN.containsMatchIn(message)) {
            scores.incrementScore(
                PaymentMode.CARD,
                PaymentModeScores.CARD
            )
        }
    }

    private fun scoreBankTransfer(
        message: String,
        scores: MutableMap<PaymentMode, Int>
    ) {

        if (PaymentPatterns.BANK_TRANSFER_PATTERN.containsMatchIn(message)) {
            scores.incrementScore(
                PaymentMode.BANK_TRANSFER,
                PaymentModeScores.BANK_TRANSFER
            )
        }
    }

    private fun scoreWallet(
        message: String,
        scores: MutableMap<PaymentMode, Int>
    ) {

        if (PaymentPatterns.WALLET_PATTERN.containsMatchIn(message)) {
            scores.incrementScore(
                PaymentMode.WALLET,
                PaymentModeScores.WALLET
            )
        }
    }

    private fun scoreAutoDebit(
        message: String,
        scores: MutableMap<PaymentMode, Int>
    ) {

        if (PaymentPatterns.AUTO_DEBIT_PATTERN.containsMatchIn(message)) {
            scores.incrementScore(
                PaymentMode.AUTO_DEBIT,
                PaymentModeScores.AUTO_DEBIT
            )
        }
    }

    private fun scoreMealCard(
        message: String,
        scores: MutableMap<PaymentMode, Int>
    ) {

        if (PaymentPatterns.MEAL_CARD_PATTERN.containsMatchIn(message)) {
            scores.incrementScore(
                PaymentMode.MEAL_CARD,
                PaymentModeScores.MEAL_CARD
            )
        }
    }

    private fun scoreCash(
        message: String,
        scores: MutableMap<PaymentMode, Int>
    ) {

        if (PaymentPatterns.CASH_PATTERN.containsMatchIn(message)) {
            scores.incrementScore(
                PaymentMode.CASH,
                PaymentModeScores.CASH
            )
        }
    }

    private fun scoreCheque(
        message: String,
        scores: MutableMap<PaymentMode, Int>
    ) {

        if (PaymentPatterns.CHEQUE_PATTERN.containsMatchIn(message)) {
            scores.incrementScore(
                PaymentMode.CHEQUE,
                PaymentModeScores.CHEQUE
            )
        }
    }

    private fun MutableMap<PaymentMode, Int>.incrementScore(
        mode: PaymentMode,
        score: Int
    ) {
        this[mode] = (this[mode] ?: 0) + score
    }
}
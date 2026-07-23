package com.example.smartexpensecalendar.new_sms_engine.extraction.direction

import com.example.smartexpensecalendar.new_sms_engine.common.confidence.ConfidenceCalculator
import com.example.smartexpensecalendar.new_sms_engine.common.enums.direction.DirectionRegex
import com.example.smartexpensecalendar.new_sms_engine.extraction.ExtractionContext
import com.example.smartexpensecalendar.new_sms_engine.extraction.direction.models.Direction
import com.example.smartexpensecalendar.new_sms_engine.extraction.direction.models.DirectionResult

class DirectionExtractor {

    fun extract(
        context: ExtractionContext
    ): DirectionResult {

        val message = context.message

        // Failed / Cancelled transactions should never infer direction
        if (DirectionRegex.NEGATIVE_REGEX.containsMatchIn(message)) {
            return DirectionResult(
                direction = Direction.UNKNOWN,
                confidence = 0f
            )
        }

        val debitScore = scoreDebit(message)
        val creditScore = scoreCredit(message)

        return when {

            debitScore > creditScore ->

                DirectionResult(
                    direction = Direction.DEBIT,
                    confidence = ConfidenceCalculator.fromScores(
                        winner = debitScore,
                        competitor = creditScore
                    )
                )

            creditScore > debitScore ->

                DirectionResult(
                    direction = Direction.CREDIT,
                    confidence = ConfidenceCalculator.fromScores(
                        winner = creditScore,
                        competitor = debitScore
                    )
                )

            else ->

                DirectionResult(
                    direction = Direction.UNKNOWN,
                    confidence = 0f
                )
        }
    }

    private fun scoreDebit(
        message: String
    ): Int {

        var score = 0

        if (DirectionRegex.DEBIT_ACTION_REGEX.containsMatchIn(message)) {
            score += DirectionScores.DEBIT_ACTION
        }

        if (DirectionRegex.TRANSACTION_ACTION_REGEX.containsMatchIn(message)) {
            score += DirectionScores.TRANSACTION_ACTION
        }

        if (DirectionRegex.AUTO_DEBIT_REGEX.containsMatchIn(message)) {
            score += DirectionScores.AUTO_DEBIT
        }

        return score
    }

    private fun scoreCredit(
        message: String
    ): Int {

        var score = 0

        if (DirectionRegex.CREDIT_ACTION_REGEX.containsMatchIn(message)) {
            score += DirectionScores.CREDIT_ACTION
        }

        if (DirectionRegex.REFUND_ACTION_REGEX.containsMatchIn(message)) {
            score += DirectionScores.REFUND_ACTION
        }

        return score
    }
}
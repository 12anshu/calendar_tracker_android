package com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.model

import com.example.smartexpensecalendar.new_sms_engine.extraction.eventtype.FinancialEventType

class FinancialEventCandidateBuilder(

    private val type: FinancialEventType
) {

    private var score = 0

    private var primaryMatched = false

    private val evidences = mutableSetOf<FinancialEventEvidence>()

    fun addPrimary(

        condition: Boolean,

        score: Int,

        evidence: FinancialEventEvidence

    ): FinancialEventCandidateBuilder {

        if (!condition) {
            return this
        }

        primaryMatched = true

        this.score += score
        evidences += evidence

        return this
    }

    fun addSupporting(

        condition: Boolean,

        score: Int,

        evidence: FinancialEventEvidence

    ): FinancialEventCandidateBuilder {

        if (!condition) {
            return this
        }

        this.score += score
        evidences += evidence

        return this
    }

    fun build(): FinancialEventCandidate? {

        if (!primaryMatched) {
            return null
        }

        return FinancialEventCandidate(
            type = type,
            confidence = calculateConfidence(score),
            evidences = evidences
        )
    }

    private fun calculateConfidence(
        score: Int
    ): Float {

        return (score.coerceAtMost(MAX_SCORE)).toFloat() / MAX_SCORE
    }

    companion object {

        private const val MAX_SCORE = 100
    }
}
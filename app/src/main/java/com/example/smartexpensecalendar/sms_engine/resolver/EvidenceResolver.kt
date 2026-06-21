package com.example.smartexpensecalendar.sms_engine.resolver

import com.example.smartexpensecalendar.sms_engine.model.Candidate
import com.example.smartexpensecalendar.sms_engine.model.ExtractionResult

object EvidenceResolver {

    fun <T> resolve(
        candidates: List<Candidate<T>>
    ): ExtractionResult<T> {

        if (candidates.isEmpty()) {
            return ExtractionResult(
                value = null,
                confidence = 0,
                score = 0,
                evidence = emptyList()
            )
        }

        val scoredCandidates =
            candidates.map { candidate ->
                val score = calculateScore(candidate)
                candidate to score
            }

        val winner =
            scoredCandidates.maxByOrNull {
                it.second
            }

        if (winner == null || winner.second <= 0) {
            return ExtractionResult(
                value = null,
                confidence = 0,
                score = 0,
                evidence = emptyList()
            )
        }

        val winnerScore =
            winner.second

        val totalScore =
            scoredCandidates.sumOf {
                it.second
            }

        val confidence =
            if (totalScore <= 0) {
                0
            } else {
                ((winnerScore.toDouble() / totalScore) * 100)
                    .toInt()
            }

        return ExtractionResult(
            value = winner.first.value,
            confidence = confidence,
            score = winnerScore,
            evidence = winner.first.evidence
        )
    }

    private fun calculateScore(
        candidate: Candidate<*>
    ): Int {

        return candidate.evidence.sumOf {
            it.score
        }
    }
}
